package ca.veltus.crewcaller.main.data.dataitem

import ca.veltus.crewcaller.main.data.dto.ContactDTO
import java.io.Serializable
import java.util.*

data class ContactDataItem(
    var firstName: String,
    var lastName: String?,
    var phone: String?,
    var position: String?,
    var email: String?,
    var facebook: String?,
    var instagram: String?,
    var production: String?,
    val id: String = UUID.randomUUID().toString()
    ) : Serializable

fun ContactDataItem.asDatabaseModel(): ContactDTO {
    return ContactDTO(
        firstName = this.firstName,
        lastName = this.lastName,
        phone = this.phone,
        position = this.position,
        email = this.email,
        facebook = this.facebook,
        instagram = this.instagram,
        production = this.production,
        id = this.id
    )
}
