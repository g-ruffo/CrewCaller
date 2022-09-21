package ca.veltus.crewcaller.main.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.veltus.crewcaller.base.BaseViewModel
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.dataitem.WeatherDataItem
import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.main.data.dto.relations.WorkDayAndPayRate
import ca.veltus.crewcaller.utils.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class HomeViewModel(val app: Application, val dataSource: CrewCallerDataSource) :
    BaseViewModel(app) {

    companion object {
        const val TAG = "HomeViewModelLog"
    }

    private var workDateTime = MutableLiveData<LocalDateTime?>()

    private val _workId = MutableLiveData<String?>()
    val workId: LiveData<String?>
        get() = _workId

    val workDate = MutableLiveData<String?>()
    val workStartTime = MutableLiveData<String?>()
    val workEndTime = MutableLiveData<String?>()
    val workLocation = MutableLiveData<String?>()
    private val workPayRateId = MutableLiveData<String?>()
    val workDayProduction = MutableLiveData<String?>()

    val totalHoursWorked = MutableLiveData<String?>()

    val totalPay = MutableLiveData<Double?>()
    val workTier = MutableLiveData<String?>()
    val workRate = MutableLiveData<String?>()
    val workPosition = MutableLiveData<String?>()

    val _weather = MutableLiveData<WeatherDataItem?>()
    val weather: LiveData<WeatherDataItem?>
        get() = _weather

    var timerRunning = MutableLiveData<Boolean>()

    var timer = Timer()

    init {
        timerRunning.value = false
        _weather.value = null
    }

    fun clearValues() {
        _workId.value = null
        workDate.value = null
        workStartTime.value = null
        workEndTime.value = null
        workLocation.value = null
        workPayRateId.value = null
        workDayProduction.value = null
        workTier.value = null
        workPosition.value = null
        workRate.value = null
        totalHoursWorked.value = null
        workDateTime.value = null
        totalPay.value = null

    }

    // Get weather at current longitude and latitude.
    fun getWeather(longitude: String, latitude: String) {
        viewModelScope.launch {
            dataSource.getWeatherForecast(longitude, latitude)
            val result = dataSource.getCurrentWeather()
            when (result) {
                is Result.Success<*> -> {

                    // If successful map retrieved database models to domain models to display in Ui.
                    val weather = (result.data as WeatherDTO).asDomainModel()
                    _weather.value = weather
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    // Get the next upcoming scheduled WorkDayAndPayRate.
    fun loadNextWorkDay() {
        val today = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now())

        viewModelScope.launch {
            val result = dataSource.loadWorkDaysAndPayRate(today)
            when (result) {
                is Result.Success<*> -> {

                    // If successful map retrieved database models to domain models to display in Ui.
                    val workDaysAndPayRatesList = result.data as List<WorkDayAndPayRate>
                    if (!workDaysAndPayRatesList.isNullOrEmpty()) {
                        var item = workDaysAndPayRatesList.lastIndex
                        // Sort items received and select the closest one to current date that has a null end time.
                        for (i in item downTo 0) {
                            if (workDaysAndPayRatesList[i].workDay.endTime == null) {
                                clearValues()
                                loadSelectedWorkDay(workDaysAndPayRatesList[i])
                                val dateParse = LocalDate.parse(workDate.value)
                                val timeParse = LocalTime.parse(workStartTime.value)
                                workDateTime.value = LocalDateTime.of(dateParse, timeParse)

                                workTimer()
                            }
                        }

                    } else {
                        clearValues()
                    }
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            invalidateShowNoData()
        }
    }

    // Load WorkDayAndPayRate into view model that was provided by loadNextWorkDay().
    private fun loadSelectedWorkDay(workDayAndPayRate: WorkDayAndPayRate?): Boolean {
        return if (workDayAndPayRate == null) {
            false
        } else {
            val workDay = workDayAndPayRate.workDay.asDomainModel()
            val payRate = workDayAndPayRate.payRate.asDomainModel()
            _workId.value = workDay.id
            workDate.value = workDay.date
            workStartTime.value = workDay.startTime
            workEndTime.value = workDay.endTime
            workLocation.value = workDay.location
            workPayRateId.value = workDay.rate
            workDayProduction.value = workDay.production
            workPayRateId.value = payRate.id
            workTier.value = payRate.tier
            workPosition.value = payRate.position
            workRate.value = payRate.rate
            true
        }
    }

    // Set the end time provided by dialog and calculate earnings.
    fun setEndTimeAndUpdateWorkDay(time: String) {
        totalHoursWorked.value = null
        workEndTime.value = time
        calculateTimeAndEarnings(null)
        viewModelScope.launch {
            dataSource.updateWorkDayEndTime(_workId.value!!, time)
        }
    }

    // Calculate time worked and earnings. If time worked is null, end time has been set and result is final.
    fun calculateTimeAndEarnings(timeWorked: Long?) {
        if (workRate.value.isNullOrEmpty()) {
            return
        }
        if (timeWorked == null) {
            totalPay.value = calculateEarnings(
                workRate.value!!,
                null,
                workStartTime.value,
                workEndTime.value
            ).first
            totalHoursWorked.value = calculateEarnings(
                workRate.value!!,
                null,
                workStartTime.value,
                workEndTime.value
            ).second

        } else {
            totalPay.value = calculateEarnings(workRate.value!!, timeWorked).first
            totalHoursWorked.value = calculateEarnings(workRate.value!!, timeWorked).second
        }
    }

    // Inform the user that there's not any data if there are no upcoming WorkDays
    private fun invalidateShowNoData() {
        showNoData.value = _workId.value == null || _workId.value!!.isEmpty()
    }

    // Check if timer is already running and if not create new timer.
    private fun workTimer() {
        cancelWorkTimer()

        val startTime = Date.from(workDateTime.value!!.atZone(ZoneId.systemDefault()).toInstant())
        val currentTime = Calendar.getInstance().time

        // If start time is before current time schedule timer.
        if (startTime.after(currentTime)) {
            timerRunning.value = true
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    viewModelScope.launch {
                        val time = Date().time - startTime.time
                        calculateTimeAndEarnings(time)
                        totalHoursWorked.value = getTimeStringFromLong(time)
                    }
                }
            }, startTime, 1000)
        } else {
            // If start time is after current time start timer.
            timerRunning.value = true
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    viewModelScope.launch {
                        val time = Date().time - startTime.time
                        calculateTimeAndEarnings(time)
                        totalHoursWorked.value = getTimeStringFromLong(time)
                    }
                }
            }, 0, 1000)
        }
    }

    // Check if timer is already running. If so, cancel and create new instance of Timer().
    fun cancelWorkTimer() {
        if (timerRunning.value == true) {
            timer.cancel()
            timer.purge()
            timer = Timer()
        }
    }

    // Generate Uri from address for intent.
    fun generateAddress(): Uri? {
        return if (workLocation.value.isNullOrEmpty()) {
            null
        } else {
            Uri.parse("google.navigation:mode=d&q=" + Uri.encode(workLocation.value))
        }
    }
}