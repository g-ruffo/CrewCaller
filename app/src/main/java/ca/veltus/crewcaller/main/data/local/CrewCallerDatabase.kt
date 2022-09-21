package ca.veltus.crewcaller.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ca.veltus.crewcaller.main.data.dto.*

@Database(entities = [ContactDTO::class, PayRateDTO::class, ProductionDTO::class, WorkDayDTO::class, WeatherDTO::class], version = 1, exportSchema = false)
abstract class CrewCallerDatabase : RoomDatabase() {
    abstract fun crewCallerDao(): CrewCallerDao
}