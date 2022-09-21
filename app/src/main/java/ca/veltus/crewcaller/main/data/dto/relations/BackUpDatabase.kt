package ca.veltus.crewcaller.main.data.dto.relations

import ca.veltus.crewcaller.main.data.dto.ContactDTO
import ca.veltus.crewcaller.main.data.dto.PayRateDTO
import ca.veltus.crewcaller.main.data.dto.ProductionDTO
import ca.veltus.crewcaller.main.data.dto.WorkDayDTO

data class BackUpDatabase(
    var date: String? = null,
    var contacts: List<ContactFirebaseObject>? = null,
    var productions: List<ProductionFirebaseObject>? = null,
    var work_days: List<WorkDayFirebaseObject>? = null,
    var pay_rates: List<PayRateFirebaseObject>? = null
)

data class ContactFirebaseObject(
    var firstName: String? = null,
    var lastName: String? = null,
    var phone: String? = null,
    var position: String? = null,
    var email: String? = null,
    var facebook: String? = null,
    var instagram: String? = null,
    var production: String? = null,
    var id: String? = null
)

fun ContactFirebaseObject.asDatabaseModel(): ContactDTO {
    return ContactDTO(
        firstName = this.firstName!!,
        lastName = this.lastName,
        phone = this.phone,
        position = this.position,
        email = this.email,
        facebook = this.facebook,
        instagram = this.instagram,
        production = this.production,
        id = this.id!!
    )
}

data class ProductionFirebaseObject(
    var name: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null,
    var id: String? = null
)

fun ProductionFirebaseObject.asDatabaseModel(): ProductionDTO {
    return ProductionDTO(
        name = this.name!!,
        address = this.address,
        phoneNumber = this.phoneNumber,
        email = this.email,
        id = this.id!!
    )
}

data class WorkDayFirebaseObject(
    var id: String? = null,
    var date: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
    var location: String? = null,
    var rate: String? = null,
    var production: String? = null
)

fun WorkDayFirebaseObject.asDatabaseModel(): WorkDayDTO {
    return WorkDayDTO(
        id = this.id!!,
        date = this.date!!,
        startTime = this.startTime,
        endTime = this.endTime,
        location = this.location,
        rate = this.rate,
        production = this.production
    )
}

data class PayRateFirebaseObject(
    var id: String? = null,
    var position: String? = null,
    var rate: String? = null,
    var tier: String? = null
)

fun PayRateFirebaseObject.asDatabaseModel(): PayRateDTO {
    return PayRateDTO(
        id = this.id!!,
        tier = this.tier!!,
        position = this.position!!,
        rate = this.rate!!
    )
}