package ca.veltus.crewcaller.main.scheduled

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseViewModel
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.dataitem.*
import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.main.data.dto.relations.WorkDayAndPayRate
import ca.veltus.crewcaller.main.scheduled.saveworkday.SaveWorkDayFragmentDirections
import ca.veltus.crewcaller.utils.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class ScheduledWorkViewModel(val app: Application, val dataSource: CrewCallerDataSource) :
    BaseViewModel(app) {

    companion object {
        const val TAG = "ScheduledWorkVMLog"
    }

    val workDayList = MutableLiveData<List<WorkDayDataItem>>()

    val _productionListArray = MutableLiveData<Array<String>>()
    val productionListArray: LiveData<Array<String>>
        get() = _productionListArray

    val _payRateListArray = MutableLiveData<List<PayRateDataItem>>()
    val payRateListArray: LiveData<List<PayRateDataItem>>
        get() = _payRateListArray

    private var workDateTime = MutableLiveData<LocalDateTime?>()

    val workId = MutableLiveData<String?>()
    val workDate = MutableLiveData<String?>()
    val workStartTime = MutableLiveData<String?>()
    val workEndTime = MutableLiveData<String?>()
    val workLocation = MutableLiveData<String?>()
    val workPayRateId = MutableLiveData<String?>()
    val workDayProduction = MutableLiveData<String?>()
    val totalHoursWorked = MutableLiveData<String?>()

    val workTier = MutableLiveData<String?>()
    val workRate = MutableLiveData<String?>()
    val workPosition = MutableLiveData<String?>()

    val enableEditing = MutableLiveData<Boolean>()

    var updateWorkDay: Boolean = false

    val totalPay = MutableLiveData<Double?>()


    init {
        enableEditing.value = true
        updateWorkDay = false

    }

    fun clearValues() {
        workId.value = null
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
        enableEditing.value = true
    }

    // Load all saved WorkDays from database.
    fun loadWorkDays() {
        viewModelScope.launch {
            val result = dataSource.getWorkDays()
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<WorkDayDataItem>()
                    dataList.addAll((result.data as List<WorkDayDTO>).map { it.asDomainModel() })
                    workDayList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }

            // Check if no data has to be shown.
            invalidateShowNoData()
        }
    }

    // Load selected WorkDay by id from database.
    fun loadSelectedWorkDayId(id: String) {
        viewModelScope.launch {
            val result = dataSource.loadWorkDayAndPayRateFromId(id)
            when (result) {
                is Result.Success<*> -> {
                    val workDayAndPayRate = result.data as WorkDayAndPayRate
                    val workDay = workDayAndPayRate.workDay.asDomainModel()
                    val payRate = workDayAndPayRate.payRate.asDomainModel()
                    workId.value = workDay.id
                    workDate.value = workDay.date
                    workStartTime.value = workDay.startTime
                    workEndTime.value = workDay.endTime
                    workLocation.value = workDay.location
                    workPayRateId.value = workDay.rate
                    workDayProduction.value = workDay.production
                    enableEditing.value = false
                    workPayRateId.value = payRate.id
                    workTier.value = payRate.tier
                    workPosition.value = payRate.position
                    workRate.value = payRate.rate

                    calculateTimeAndEarnings()

                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            // Check if no data has to be shown.
            invalidateShowNoData()
        }
    }

    // Calculate time worked and total earnings for selected WorkDay.
    private fun calculateTimeAndEarnings() {
        totalPay.value =
            calculateEarnings(workRate.value!!, null, workStartTime.value, workEndTime.value).first
        totalHoursWorked.value =
            calculateEarnings(workRate.value!!, null, workStartTime.value, workEndTime.value).second
    }

    // Get a list of saved productions to be displayed in spinner.
    fun getProductionsList() {
        viewModelScope.launch {
            val result = dataSource.getProductionList()
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<String>()
                    dataList.addAll((result.data as Array<String>).map { it })
                    _productionListArray.value = dataList.toTypedArray()
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    // Get a list of saved pay rates to be displayed in spinner.
    fun getPayRateList() {
        viewModelScope.launch {
            val result = dataSource.getPayRates()
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<PayRateDataItem>()
                    dataList.addAll((result.data as List<PayRateDTO>).map { it.asDomainModel() })
                    _payRateListArray.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    fun deleteWorkDay() {
        viewModelScope.launch {
            dataSource.deleteWorkDay(workId.value!!)
            showToast.value = app.getString(R.string.workDayDeletedToast)
            navigationCommand.postValue(NavigationCommand.BackTo(R.id.scheduledWorkFragment))
            clearValues()
        }
    }

    fun navigateToAddNewFragment() {
        navigationCommand.postValue(
            NavigationCommand.To(
                ScheduledWorkFragmentDirections.actionScheduledWorkFragmentToSaveWorkDayFragment(
                    null
                )
            )
        )
    }

    fun navigateBack() {
        navigationCommand.postValue(NavigationCommand.Back)
    }

    // Create WorkDayDataItem from input fields and validate before saving.
    fun saveWorkDay() {
        val id = workId.value ?: UUID.randomUUID().toString()
        val date = workDate.value
        val startTime = workStartTime.value
        val endTime = workEndTime.value
        val location = workLocation.value
        val rate = workPayRateId.value
        val production = workDayProduction.value

        val workDayItem =
            WorkDayDataItem(id, date!!, startTime, endTime, location, rate, production)

        validateAndSaveWorkDay(workDayItem)
    }

    // Check that all required data is valid and if so save to database.
    private fun validateAndSaveWorkDay(workDay: WorkDayDataItem): Boolean {
        workDay.location = workDay.location?.trimSpace()
        if (workDay.production.isNullOrEmpty()) {
            showSnackBar.value = "A Production is Required"
            return false
        }
        if (workDay.date.isNullOrEmpty()) {
            showSnackBar.value = "A Date is Required"
            return false
        }
        if (workDay.rate.isNullOrEmpty()) {
            showSnackBar.value = "A Pay Rate is Required"
            return false
        }
        if (workDay.startTime.isNullOrEmpty()) {
            showSnackBar.value = "Start Time is Required"
            return false
        }
        viewModelScope.launch {
            dataSource.saveWorkDay(workDay.asDatabaseModel())
            showToast.value = "Work Day Saved"
        }

        // If user is updating an existing WorkDay, navigate back to detail page.
        if (updateWorkDay) {
            navigationCommand.postValue(
                NavigationCommand.To(
                    SaveWorkDayFragmentDirections.actionSaveWorkDayFragmentToDetailWorkDayFragment(
                        workDay.id
                    )
                )
            )
        } else {
            navigateBack()
        }
        return true
    }

    fun setStartTime(time: String) {
        workStartTime.value = time
    }

    fun setEndTime(time: String) {
        workEndTime.value = time
    }

    fun setWorkDate(date: String) {
        workDate.value = date
    }

    // Inform the user that there's not any data if the productionsList is empty
    private fun invalidateShowNoData() {
        showNoData.value = workDayList.value == null || workDayList.value!!.isEmpty()
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