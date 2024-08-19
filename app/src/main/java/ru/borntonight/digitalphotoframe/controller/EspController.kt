package ru.borntonight.digitalphotoframe.controller

import retrofit2.Call
import retrofit2.http.GET

interface EspController {
    @GET("temperature")
    fun getTemperature(): Call<String>

    @GET("humidity")
    fun getHumidity(): Call<String>
}