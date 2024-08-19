package ru.borntonight.digitalphotoframe.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.borntonight.digitalphotoframe.controller.RetrofitControllers
import ru.borntonight.digitalphotoframe.controller.RetrofitControllers.BASE_URL_WEATHER
import ru.borntonight.digitalphotoframe.dto.Geolocation
import ru.borntonight.digitalphotoframe.dto.weather.CurrentWeatherResponse
import ru.borntonight.digitalphotoframe.util.AppConstants.API_WEATHER_TOKEN
import ru.borntonight.digitalphotoframe.util.AppConstants.CITY_GEOLOCATION_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.GEOLOCATION_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.SHARED_PREF_VALUE
import java.io.IOException

class WeatherUtils {

    public fun sendOutsideWeatherRequest(
        context: Context,
        updateOutsideWeather: (current: CurrentWeatherResponse, currentCity: String) -> Unit
    ) {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE)
        var geolocation = Geolocation()
        var currentCity = ""
        if (sharedPreferences.contains(GEOLOCATION_KEY)) {
            geolocation = Gson().fromJson(
                sharedPreferences.getString(GEOLOCATION_KEY, ""),
                Geolocation::class.java
            )
            currentCity = "Геопозиция"
        }
        if (geolocation.lat.isNullOrEmpty() || geolocation.lon.isNullOrEmpty()) {
            val cityGeolocationEnum = Gson().fromJson(
                sharedPreferences.getString(CITY_GEOLOCATION_KEY, ""),
                CityGeolocationEnum::class.java
            )
            geolocation = cityGeolocationEnum.geolocation
            currentCity = cityGeolocationEnum.cityName
        }

        val callCurrentOutsideWeather = RetrofitControllers.weatherController.getCurrentWeather(
            geolocation.lat,
            geolocation.lon,
            API_WEATHER_TOKEN,
        )
        callCurrentOutsideWeather.enqueue(object : Callback<CurrentWeatherResponse> {
            override fun onResponse(
                call: Call<CurrentWeatherResponse>,
                response: Response<CurrentWeatherResponse>
            ) {
                if (response.code() == 200) {
                    response.body()?.let { updateOutsideWeather(it, currentCity) }
                }
            }

            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                Log.e("Retrofit Error", t.message.toString())
            }
        })
    }

    public fun sendInsideWeatherRequest(updateInsideWeather: (temperature: String, humidity: String) -> Unit) {

        val callTemperature = RetrofitControllers.espController.getTemperature(
        )
        callTemperature.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.code() == 200) {
                    response.body()?.let { updateInsideWeather(it, "") }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Retrofit Error", t.message.toString())
            }
        })
        val callHumidity = RetrofitControllers.espController.getHumidity(
        )
        callHumidity.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.code() == 200) {
                    response.body()?.let { updateInsideWeather("", it) }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Retrofit Error", t.message.toString())
            }
        })
    }

    companion object {
        fun getWindDirection(angle: Int): String {
            val directions = arrayOf("↓ С", "↙ СВ", "← В", "↖ ЮВ", "↑ ,", "↗ ЮЗ", "→ З", "↘ СЗ")
            return directions[(Math.round((angle / 45).toDouble()) % 8).toInt()]
        }
    }
}