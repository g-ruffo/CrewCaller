package ca.veltus.crewcaller.main.data.dto.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.veltus.crewcaller.main.data.dto.ContactDTO
import ca.veltus.crewcaller.main.data.dto.ProductionDTO
import ca.veltus.crewcaller.main.data.dto.WorkDayDTO

data class ProductionWithWorkDaysWithContacts(
    @Embedded val production: ProductionDTO,
    @Relation(parentColumn = "production", entityColumn = "production")
    val workDays: List<WorkDayDTO>,
    @Relation(parentColumn = "production", entityColumn = "production")
    val contacts: List<ContactDTO>
)