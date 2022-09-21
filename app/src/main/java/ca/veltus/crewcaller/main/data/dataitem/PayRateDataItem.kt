package ca.veltus.crewcaller.main.data.dataitem

import ca.veltus.crewcaller.main.data.dto.PayRateDTO
import java.io.Serializable
import java.util.*

data class PayRateDataItem(
    val id: String = UUID.randomUUID().toString(),
    var tier: String,
    var position: String,
    var rate: String
) : Serializable {

    // Set the display string for PayRate spinner list.
    override fun toString(): String {
        return ("$tier - $position   =   $rate")
    }
}

fun PayRateDataItem.asDatabaseModel(): PayRateDTO {
    return PayRateDTO(
        id = this.id,
        tier = this.tier,
        position = this.position,
        rate = this.rate
    )
}


