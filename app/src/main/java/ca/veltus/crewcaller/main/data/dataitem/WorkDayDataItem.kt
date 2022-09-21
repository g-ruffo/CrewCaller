package ca.veltus.crewcaller.main.data.dataitem

import ca.veltus.crewcaller.main.data.dto.WorkDayDTO
import java.io.Serializable
import java.util.*

data class WorkDayDataItem(
    var id: String = UUID.randomUUID().toString(),
    var date: String,
    var startTime: String?,
    var endTime: String?,
    var location: String?,
    var rate: String?,
    var production: String?
) : Serializable

fun WorkDayDataItem.asDatabaseModel(): WorkDayDTO {
    return WorkDayDTO(
        id = this.id,
        date = this.date,
        startTime = this.startTime,
        endTime = this.endTime,
        location = this.location,
        rate = this.rate,
        production = this.production

    )
}
