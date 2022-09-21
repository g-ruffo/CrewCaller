package ca.veltus.crewcaller.main.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.veltus.crewcaller.main.data.dataitem.WeatherDataItem

@Entity(tableName = "current_weather")
data class WeatherDTO(
    @PrimaryKey
    val id: Int,
    val dateTime: Int,
    val timezone: Int,
    val city: String,
    val country: String,
    val temperature: Double,
    val feelsLikeTemperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val currentConditions: String,
    val detailConditions: String,
    val icon: String
)

fun WeatherDTO.asDomainModel(): WeatherDataItem {
    return WeatherDataItem(
        id = this.id,
        dateTime = this.dateTime,
        timezone = this.timezone,
        city = this.city,
        country = this.country,
        temperature = this.temperature,
        feelsLikeTemperature = this.feelsLikeTemperature,
        minTemperature = this.minTemperature,
        maxTemperature = this.maxTemperature,
        currentConditions = this.currentConditions,
        detailConditions = this.detailConditions,
        icon = this.icon
    )
}



