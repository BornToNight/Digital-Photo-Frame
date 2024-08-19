package ru.borntonight.digitalphotoframe.controller

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.borntonight.digitalphotoframe.dto.weather.CurrentWeatherResponse

interface WeatherController {
    @GET("data/2.5/weather?")
    fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appId: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru",
    ): Call<CurrentWeatherResponse>
}