package ca.veltus.crewcaller.main.data.local

import android.content.Context
import androidx.room.Room

// Singleton class that is used to create a CrewCaller db
object LocalDB {

    // Static method that creates a class and returns the DAO of the objects
    fun createCrewCallerDao(context: Context): CrewCallerDao {
        return Room.databaseBuilder(
            context.applicationContext,
            CrewCallerDatabase::class.java, "crewCaller.db"
        ).build().crewCallerDao()
    }
}