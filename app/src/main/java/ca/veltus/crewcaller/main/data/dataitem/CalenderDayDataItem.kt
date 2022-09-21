package ca.veltus.crewcaller.main.data.dataitem

import java.io.Serializable

data class CalenderDayDataItem(
    var day: String,
    var worked: Boolean = false
) : Serializable
