package ca.veltus.crewcaller.main.calendar

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.veltus.crewcaller.base.BaseViewModel
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.dataitem.CalenderDayDataItem
import ca.veltus.crewcaller.main.data.dataitem.WorkDayDataItem
import ca.veltus.crewcaller.main.data.dto.Result
import ca.veltus.crewcaller.main.data.dto.WorkDayDTO
import ca.veltus.crewcaller.main.data.dto.asDomainModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarViewModel(val app: Application, val dataSource: CrewCallerDataSource) :
    BaseViewModel(app) {

    companion object {
        const val TAG = "CalendarViewModel"
    }

    val calendarList = MutableLiveData<List<CalenderDayDataItem>?>()

    val _workDayList = MutableLiveData<List<WorkDayDataItem>?>()
    val workDayList: LiveData<List<WorkDayDataItem>?>
        get() = _workDayList

    private var selectedDate = LocalDate.now()

    val monthYearTextFromDate = MutableLiveData<String?>()

    init {
        // Set starting calendar month to current month
        selectedDate = LocalDate.now()
    }

    fun clearValues() {
        calendarList.value = null
        _workDayList.value = null
        monthYearTextFromDate.value = null
    }

    // Load all saved WorkDays from Room.
    fun loadWorkDays() {
        viewModelScope.launch {
            val result = dataSource.getWorkDays()
            when (result) {
                is Result.Success<*> -> {
                    // If successful map the database model to domain model for display in the Ui
                    val dataList = ArrayList<WorkDayDataItem>()
                    dataList.addAll((result.data as List<WorkDayDTO>).map { workDay ->
                        workDay.asDomainModel()
                    })
                    _workDayList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    // Set the calendar month and retrieve the days needed for display in the Ui
    fun setCalendarMonth() {
        val daysInMonth = getDaysInMonth(selectedDate)
        calendarList.postValue(daysInMonth)
        monthYearTextFromDate.value = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    // Calculate the days in the provided month and return a list of CalendarDayDataItems
    private fun getDaysInMonth(date: LocalDate): ArrayList<CalenderDayDataItem> {
        val calenderDaysArrayList = ArrayList<CalenderDayDataItem>()
        val yearMonth = YearMonth.from(date)
        val numberOfDaysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonthDate = selectedDate.withDayOfMonth(1)
        var dayOfWeek = firstOfMonthDate.dayOfWeek.value

        // If the month begins on day 7, set dayOfWeek to 0 to shift all rows to top of grid.
        if (dayOfWeek == 7) dayOfWeek = 0

        for (i in 1..42) {
            if (i <= dayOfWeek || i > numberOfDaysInMonth + dayOfWeek) {

                // If day is not in provided month insert empty cell.
                val day = CalenderDayDataItem("")

                calenderDaysArrayList.add(day)
            } else {
                val day = CalenderDayDataItem((i - dayOfWeek).toString())

                // If calendar day shares date with any of the saved WorkDays, set worked value to true.
                day.worked =
                    _workDayList.value!!.any {
                        it.date == "$yearMonth-${
                            String.format(
                                "%02d",
                                day.day.toInt()
                            )
                        }"
                    }

                calenderDaysArrayList.add(day)
            }
        }
        return calenderDaysArrayList
    }

    fun previousMonth() {
        selectedDate = selectedDate.minusMonths(1)
    }


    fun nextMonth() {
        selectedDate = selectedDate.plusMonths(1)
    }
}