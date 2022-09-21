package ca.veltus.crewcaller.main.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.veltus.crewcaller.main.data.dataitem.PayRateDataItem

@Entity(tableName = "pay_rate")
data class PayRateDTO(
    @PrimaryKey
    @ColumnInfo(name = "pay_id") val id: String,
    @ColumnInfo(name = "tier") val tier: String,
    @ColumnInfo(name = "position") val position: String,
    @ColumnInfo(name = "pay_rate") val rate: String
)

fun PayRateDTO.asDomainModel(): PayRateDataItem {
    return PayRateDataItem(
        id = this.id,
        tier = this.tier,
        position = this.position,
        rate = this.rate
    )
}

