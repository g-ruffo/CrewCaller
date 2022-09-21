package ca.veltus.crewcaller.main.data.dataitem

data class WeatherDataItem(
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