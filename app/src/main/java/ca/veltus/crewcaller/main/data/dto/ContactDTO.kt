package ca.veltus.crewcaller.main.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.veltus.crewcaller.main.data.dataitem.ContactDataItem

@Entity(tableName = "contacts")
data class ContactDTO(
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String?,
    @ColumnInfo(name = "phone_number") val phone: String?,
    @ColumnInfo(name = "position") val position: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "facebook") val facebook: String?,
    @ColumnInfo(name = "instagram") val instagram: String?,
    @ColumnInfo(name = "production") val production: String?,
    @PrimaryKey @ColumnInfo(name = "contact_id") val id: String
)

fun ContactDTO.asDomainModel(): ContactDataItem {
    return ContactDataItem(
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




