package ca.veltus.crewcaller.main.data

import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.main.data.dto.relations.ProductionWithWorkDaysWithContacts
import ca.veltus.crewcaller.main.data.dto.relations.WorkDayAndPayRate

class FakeCrewCallerDataSource(
    var contacts: MutableList<ContactDTO>? = mutableListOf(),
    var workDays: MutableList<WorkDayDTO>? = mutableListOf(),
    var productions: MutableList<ProductionDTO>?  = mutableListOf(),
    var payRates: MutableList<PayRateDTO>? = mutableListOf()
): CrewCallerDataSource {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getContacts(): Result<List<ContactDTO>> {
        return if (shouldReturnError) {
            Result.Error("No Contacts Found")
        } else {
            contacts.let { Result.Success(ArrayList(it!!)) }
        }
    }

    override suspend fun getContactIdList(id: String): Result<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getContactsFromProduction(productionName: String): Result<List<ContactDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPayRates(): Result<List<PayRateDTO>> {
        return if (shouldReturnError) {
            Result.Error("No PayRates Found")
        } else {
            payRates.let { Result.Success(ArrayList(it!!)) }
        }
    }

    override suspend fun getWorkDaysForProduction(production: String): Result<List<WorkDayDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductions(): Result<List<ProductionDTO>> {
        return if (shouldReturnError) {
            Result.Error("No Productions Found")
        } else {
            productions.let { Result.Success(ArrayList(it!!)) }
        }
    }


    override suspend fun getProductionList(): Result<Array<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkDays(): Result<List<WorkDayDTO>> {
        return if (shouldReturnError) {
            Result.Error("No WorkDays Found")
        } else {
            workDays.let { Result.Success(ArrayList(it!!)) }
        }
    }


    override suspend fun getContact(id: String): Result<ContactDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getPayRate(id: String): Result<PayRateDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getProduction(id: String): Result<ProductionDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkDay(id: String): Result<WorkDayDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun saveContact(contactDTO: ContactDTO) {
        contacts?.add(contactDTO)
    }

    override suspend fun savePayRate(payRateDTO: PayRateDTO) {
        payRates?.add(payRateDTO)
    }

    override suspend fun saveProduction(productionDTO: ProductionDTO) {
        productions?.add(productionDTO)
    }

    override suspend fun saveWorkDay(workDayDTO: WorkDayDTO) {
        workDays?.add(workDayDTO)
    }

    override suspend fun deleteAllContacts() {
        contacts?.clear()
    }

    override suspend fun deleteAllPayRates() {
        payRates?.clear()
    }

    override suspend fun deleteAllProductions() {
        productions?.clear()
    }

    override suspend fun deleteAllWorkDays() {
        workDays?.clear()
    }

    override suspend fun deleteContact(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePayRate(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduction(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWorkDay(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkDayEndTime(id: String, time: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherForecast(longitude: String, latitude: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentWeather(): Result<WeatherDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun loadProductionWithWorkDaysWithContacts(productionId: String): Result<ProductionWithWorkDaysWithContacts> {
        TODO("Not yet implemented")
    }

    override suspend fun loadWorkDaysAndPayRate(date: String): Result<List<WorkDayAndPayRate>> {
        TODO("Not yet implemented")
    }

    override suspend fun loadWorkDayAndPayRateFromId(workDayId: String): Result<WorkDayAndPayRate> {
        TODO("Not yet implemented")
    }
}