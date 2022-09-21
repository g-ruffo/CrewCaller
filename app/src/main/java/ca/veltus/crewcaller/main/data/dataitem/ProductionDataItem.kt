package ca.veltus.crewcaller.main.data.dataitem

import ca.veltus.crewcaller.main.data.dto.ProductionDTO
import java.io.Serializable
import java.util.*

data class ProductionDataItem(
    var name: String,
    var address: String?,
    var phoneNumber: String?,
    var email: String?,
    val id: String = UUID.randomUUID().toString(),
) : Serializable

fun ProductionDataItem.asDatabaseModel(): ProductionDTO {
    return ProductionDTO(
        name = this.name,
        address = this.address,
        phoneNumber = this.phoneNumber,
        email = this.email,
        id = this.id
    )
}

