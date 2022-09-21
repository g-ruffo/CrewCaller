package ca.veltus.crewcaller.main.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.veltus.crewcaller.main.data.dataitem.WorkDayDataItem

@Entity(tableName = "work_day")
data class WorkDayDTO(
    @PrimaryKey
    @ColumnInfo(name = "work_id") val id: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "start_time") val startTime: String?,
    @ColumnInfo(name = "end_time") val endTime: String?,
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "pay_rate") val rate: String?,
    @ColumnInfo(name = "production") val production: String?
)

fun WorkDayDTO.asDomainModel(): WorkDayDataItem {
    return WorkDayDataItem(
        id = this.id,
        date = this.date,
        startTime = this.startTime,
        endTime = this.endTime,
        location = this.location,
        rate = this.rate,
        production = this.production
    )
}
