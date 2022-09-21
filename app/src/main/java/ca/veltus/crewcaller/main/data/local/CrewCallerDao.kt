package ca.veltus.crewcaller.main.data.local

import androidx.room.*
import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.main.data.dto.relations.ProductionWithWorkDaysWithContacts
import ca.veltus.crewcaller.main.data.dto.relations.WorkDayAndPayRate

@Dao
interface CrewCallerDao {

    /**
     * Query Database and return a list of objects
     */

    // Return all Contacts
    @Query("SELECT * FROM contacts")
    suspend fun getContacts(): List<ContactDTO>

    // Return all PayRates
    @Query("SELECT * FROM pay_rate")
    suspend fun getPayRates(): List<PayRateDTO>

    // Return all Productions
    @Query("SELECT * FROM productions")
    suspend fun getProductions(): List<ProductionDTO>

    // Return list of Production Names
    @Query("SELECT production FROM productions")
    suspend fun getProductionList(): Array<String>

    // Return all WorkDays
    @Query("SELECT * FROM work_day ORDER BY date DESC")
    suspend fun getWorkDays(): List<WorkDayDTO>

    // Return all WorkDays for Production
    @Query("SELECT * FROM work_day WHERE production = :production")
    suspend fun getWorkDaysForProduction(production: String): List<WorkDayDTO>

    // Return Weather Data
    @Query("SELECT * FROM current_weather")
    suspend fun getCurrentWeather(): WeatherDTO

    // Return Contact
    @Query("SELECT * FROM contacts WHERE contact_id = :contactId")
    suspend fun getContactById(contactId: String): ContactDTO?

    // Return all Contact Ids from Production Id
    @Query("SELECT contact_id FROM contacts WHERE production = :productionId")
    suspend fun getContactIdList(productionId: String): List<String>

    // Return all Contacts from List of Ids
    @Query("SELECT * FROM contacts WHERE production = :productionName")
    suspend fun getContactsFromProduction(productionName: String): List<ContactDTO>

    // Return PayRate
    @Query("SELECT * FROM pay_rate WHERE pay_id = :payRateId")
    suspend fun getPayRateById(payRateId: String): PayRateDTO?

    // Return Production
    @Query("SELECT * FROM productions WHERE production_id = :productionId")
    suspend fun getProductionById(productionId: String): ProductionDTO?

    // Return WorkDay
    @Query("SELECT * FROM work_day WHERE work_id = :workDayId")
    suspend fun getWorkDayById(workDayId: String): WorkDayDTO?

    /**
     * Insert object and save to Database
     */

    // Save Contact
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContact(contactDTO: ContactDTO)

    // Save PayRate
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePayRate(payRateDTO: PayRateDTO)

    // Save Production
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProduction(productionDTO: ProductionDTO)

    // Save WorkDay
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWorkDay(workDayDTO: WorkDayDTO)

    // Save WorkDay
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrentWeather(weatherDTO: WeatherDTO)

    /**
     * Delete all objects from Database.
     */

    // Delete all Contacts
    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()

    // Delete all PayRate
    @Query("DELETE FROM pay_rate")
    suspend fun deleteAllPayRates()

    // Delete all Productions
    @Query("DELETE FROM productions")
    suspend fun deleteAllProductions()

    // Delete all WorkDays
    @Query("DELETE FROM work_day")
    suspend fun deleteAllWorkDays()

    /**
     * Delete all objects from Database with the given id
     */

    // Delete Contact
    @Query("DELETE FROM contacts WHERE contact_id = :contactId")
    suspend fun deleteContact(contactId: String)

    // Delete PayRate
    @Query("DELETE FROM pay_rate WHERE pay_id = :payRateId")
    suspend fun deletePayRate(payRateId: String)

    // Delete Production
    @Query("DELETE FROM productions WHERE production_id = :productionId")
    suspend fun deleteProduction(productionId: String)

    // Delete WorkDay
    @Query("DELETE FROM work_day WHERE work_id = :workDayId")
    suspend fun deleteWorkDay(workDayId: String)

    /**
     * Update objects from Database with the given id
     */

    // Update WorkDay end time.
    @Query("UPDATE work_day SET end_time = :time WHERE work_id = :id")
    suspend fun updateWorkDayEndTime(id: String, time: String)

    /**
     * Query multiple tables for relational data
     */

    @Transaction
    @Query("SELECT * FROM productions WHERE production_id = :productionId")
    suspend fun loadProductionWithWorkDaysWithContacts(productionId: String): ProductionWithWorkDaysWithContacts

    @Transaction
    @Query("SELECT * FROM work_day WHERE date >= :today ORDER BY date ASC")
    suspend fun loadWorkDaysAndPayRate(today: String): List<WorkDayAndPayRate>

    @Transaction
    @Query("SELECT * FROM work_day WHERE work_id = :workDayId")
    suspend fun loadWorkDayAndPayRateFromId(workDayId: String): WorkDayAndPayRate

}