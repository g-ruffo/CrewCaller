package ca.veltus.crewcaller.main.data

import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.main.data.dto.relations.ProductionWithWorkDaysWithContacts
import ca.veltus.crewcaller.main.data.dto.relations.WorkDayAndPayRate


// Main entry point for accessing object data.
interface CrewCallerDataSource {
    suspend fun getContacts(): Result<List<ContactDTO>>
    suspend fun getContactIdList(id: String): Result<List<String>>
    suspend fun getContactsFromProduction(productionName: String): Result<List<ContactDTO>>
    suspend fun getPayRates(): Result<List<PayRateDTO>>
    suspend fun getWorkDaysForProduction(production: String): Result<List<WorkDayDTO>>
    suspend fun getProductions(): Result<List<ProductionDTO>>
    suspend fun getProductionList(): Result<Array<String>>
    suspend fun getWorkDays(): Result<List<WorkDayDTO>>
    suspend fun getContact(id: String): Result<ContactDTO>
    suspend fun getPayRate(id: String): Result<PayRateDTO>
    suspend fun getProduction(id: String): Result<ProductionDTO>
    suspend fun getWorkDay(id: String): Result<WorkDayDTO>
    suspend fun saveContact(contactDTO: ContactDTO)
    suspend fun savePayRate(payRateDTO: PayRateDTO)
    suspend fun saveProduction(productionDTO: ProductionDTO)
    suspend fun saveWorkDay(workDayDTO: WorkDayDTO)
    suspend fun deleteAllContacts()
    suspend fun deleteAllPayRates()
    suspend fun deleteAllProductions()
    suspend fun deleteAllWorkDays()
    suspend fun deleteContact(id: String)
    suspend fun deletePayRate(id: String)
    suspend fun deleteProduction(id: String)
    suspend fun deleteWorkDay(id: String)
    suspend fun updateWorkDayEndTime(id: String, time: String)
    suspend fun getWeatherForecast(longitude: String, latitude: String)
    suspend fun getCurrentWeather(): Result<WeatherDTO>
    suspend fun loadProductionWithWorkDaysWithContacts(productionId: String): Result<ProductionWithWorkDaysWithContacts>
    suspend fun loadWorkDaysAndPayRate(date: String): Result<List<WorkDayAndPayRate>>
    suspend fun loadWorkDayAndPayRateFromId(workDayId: String): Result<WorkDayAndPayRate>
}