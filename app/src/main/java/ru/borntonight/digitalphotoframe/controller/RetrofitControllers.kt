package ru.borntonight.digitalphotoframe.controller

import ru.borntonight.digitalphotoframe.util.RetrofitClient

object RetrofitControllers {
    const val BASE_URL_WEATHER = "https://ru.api.openweathermap.org/"
    private const val BASE_URL_ESP = "http://192.168.0.112/"
    val weatherController: WeatherController
        get() = RetrofitClient.getClient(BASE_URL_WEATHER).create(WeatherController::class.java)
    val espController: EspController
        get() = RetrofitClient.getScalarClient(BASE_URL_ESP).create(EspController::class.java)
}