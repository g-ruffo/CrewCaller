package ca.veltus.crewcaller.main.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.veltus.crewcaller.main.data.dataitem.ProductionDataItem

@Entity(tableName = "productions")
data class ProductionDTO(
    @PrimaryKey
    @ColumnInfo(name = "production") val name: String,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "phone_number") val phoneNumber: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "production_id") val id: String
)

fun ProductionDTO.asDomainModel(): ProductionDataItem {
    return ProductionDataItem(
        name = this.name,
        address = this.address,
        phoneNumber = this.phoneNumber,
        email = this.email,
        id = this.id
    )
}


