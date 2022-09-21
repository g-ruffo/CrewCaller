package ca.veltus.crewcaller.main.data.network

import ca.veltus.crewcaller.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.openweathermap.org/"
private const val API_KEY = BuildConfig.WEATHER_API_KEY

interface WeatherApiService {
    @GET("/data/2.5/weather?")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") key: String = API_KEY,
        @Query("units") units: String = "metric"
    ): WeatherParsedData
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }

    private val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)

    private val okHttp =
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.MINUTES).readTimeout(30, TimeUnit.SECONDS).build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .client(okHttp)
        .build()
}