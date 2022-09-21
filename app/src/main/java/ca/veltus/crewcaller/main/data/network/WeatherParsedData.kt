package ca.veltus.crewcaller.main.data.network

import ca.veltus.crewcaller.main.data.dto.WeatherDTO
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherParsedData(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "dt") val dateTime: Int = 0,
    @Json(name = "timezone") val timezone: Int = 0,
    @Json(name = "name") val city: String = "",
    @Json(name = "main") val weatherData: Main = Main(),
    @Json(name = "sys") val area: Sys = Sys(),
    @Json(name = "weather") val weatherConditions: List<Weather> = listOf()
) {
    data class Main(
        val feels_like: Double = 0.0,
        val humidity: Int = 0,
        val pressure: Int = 0,
        val temp: Double = 0.0,
        val temp_max: Double = 0.0,
        val temp_min: Double = 0.0
    )

    data class Sys(
        val country: String = "",
        val id: Int = 0,
        val sunrise: Int = 0,
        val sunset: Int = 0,
        val type: Int = 0
    )

    data class Weather(
        val description: String = "",
        val icon: String = "",
        val id: Int = 0,
        val main: String = ""
    )

}

fun WeatherParsedData.asDatabaseModel(): WeatherDTO {
    return WeatherDTO(
        id = this.id,
        dateTime = this.dateTime,
        timezone = this.timezone,
        city = this.city,
        country = this.area.country,
        temperature = this.weatherData.temp,
        feelsLikeTemperature = this.weatherData.feels_like,
        minTemperature = this.weatherData.temp_min,
        maxTemperature = this.weatherData.temp_max,
        currentConditions = this.weatherConditions.first().main,
        detailConditions = this.weatherConditions.first().description,
        icon = this.weatherConditions.first().icon
    )
}