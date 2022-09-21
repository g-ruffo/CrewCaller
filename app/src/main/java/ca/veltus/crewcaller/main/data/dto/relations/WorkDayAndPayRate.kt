package ca.veltus.crewcaller.main.data.dto.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.veltus.crewcaller.main.data.dto.PayRateDTO
import ca.veltus.crewcaller.main.data.dto.WorkDayDTO

data class WorkDayAndPayRate(
    @Embedded val workDay: WorkDayDTO,
    @Relation(
        parentColumn = "pay_rate",
        entityColumn = "pay_id"
    )
    val payRate: PayRateDTO
)