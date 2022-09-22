package ca.veltus.crewcaller.main.data

import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.main.data.dto.relations.ProductionWithWorkDaysWithContacts
import ca.veltus.crewcaller.main.data.dto.relations.WorkDayAndPayRate

class FakeCrewCallerDataSource: CrewCallerDataSource {

    override suspend fun getContacts(): Result<List<ContactDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getContactIdList(id: String): Result<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getContactsFromProduction(productionName: String): Result<List<ContactDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPayRates(): Result<List<PayRateDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkDaysForProduction(production: String): Result<List<WorkDayDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductions(): Result<List<ProductionDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductionList(): Result<Array<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkDays(): Result<List<WorkDayDTO>> {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override suspend fun savePayRate(payRateDTO: PayRateDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun saveProduction(productionDTO: ProductionDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun saveWorkDay(workDayDTO: WorkDayDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllContacts() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllPayRates() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllProductions() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllWorkDays() {
        TODO("Not yet implemented")
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