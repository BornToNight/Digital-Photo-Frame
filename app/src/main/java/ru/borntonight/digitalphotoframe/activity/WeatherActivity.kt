package ru.borntonight.digitalphotoframe.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Html.FROM_HTML_OPTION_USE_CSS_COLORS
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.borntonight.digitalphotoframe.R
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_UPDATE_DATE_AND_TIME_DELAY
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_UPDATE_WEATHER_DELAY
import ru.borntonight.digitalphotoframe.util.AppConstants.SHARED_PREF_VALUE
import ru.borntonight.digitalphotoframe.util.AppConstants.UPDATE_WEATHER_DELAY_KEY
import ru.borntonight.digitalphotoframe.util.DateAndTimeUtils
import ru.borntonight.digitalphotoframe.util.SlideShowUtils
import ru.borntonight.digitalphotoframe.util.WeatherUtils
import java.time.Instant
import java.time.ZoneOffset
import kotlin.math.roundToInt

class WeatherActivity : AppCompatActivity() {
    private lateinit var insideWeather: TextView
    private lateinit var date: TextView
    private lateinit var weatherIconDescription: TextView
    private lateinit var outsideWeather: TextView
    private lateinit var currentDay: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var photoInWeather: ImageView
    private lateinit var photoProgressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var slideShowUtils: SlideShowUtils

    private val handler = Handler(Looper.getMainLooper())

    private var temperature = ""
    private var humidity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.insetsController?.hide(WindowInsets.Type.navigationBars())

        insideWeather = findViewById(R.id.insideWeather)
        date = findViewById(R.id.date)
        weatherIconDescription = findViewById(R.id.weatherIconDescription)
        outsideWeather = findViewById(R.id.outsideWeather)
        currentDay = findViewById(R.id.currentDay)
        weatherIcon = findViewById(R.id.weatherIcon)
        photoInWeather = findViewById(R.id.photoInWeather)
        photoProgressBar = findViewById(R.id.photoProgressBar)

        sharedPreferences = getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE)

        val updateWeatherDelay = sharedPreferences.getLong(
            UPDATE_WEATHER_DELAY_KEY, DEFAULT_UPDATE_WEATHER_DELAY
        )

        slideShowUtils = SlideShowUtils()

        slideShowUtils.selectImageFolder(this, lifecycleScope, photoProgressBar, photoInWeather)

        lifecycleScope.launch {
            updateDateAndTime()
            SlideShowUtils().updateBrightness(this@WeatherActivity)
        }

        lifecycleScope.launch {
            updateOutsideWeather()
        }

        lifecycleScope.launch {
            updateInsideWeather()
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                updateDateAndTime()
                SlideShowUtils().updateBrightness(this@WeatherActivity)
                handler.postDelayed(this, DEFAULT_UPDATE_DATE_AND_TIME_DELAY)
            }
        }, DEFAULT_UPDATE_DATE_AND_TIME_DELAY)

        handler.postDelayed(object : Runnable {
            override fun run() {
                lifecycleScope.launch {
                    updateOutsideWeather()
                    updateInsideWeather()
                }
                handler.postDelayed(this, updateWeatherDelay)
            }
        }, updateWeatherDelay)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            slideShowUtils.stopSlideshow()
            finish()
        }
        return false
    }

    private fun updateDateAndTime() {
        date.text = DateAndTimeUtils().getDateAndTime()
        currentDay.text = DateAndTimeUtils().getCurrentDay()
    }

    private fun updateInsideWeather() {
        WeatherUtils().sendInsideWeatherRequest { temperatureResponse, humidityResponse ->
            if (temperatureResponse.isNotBlank()) temperature = temperatureResponse
            if (humidityResponse.isNotBlank()) humidity = humidityResponse
            val insideWeatherText =
                "температура: $temperature ℃\n" +
                        "влажность:   $humidity%"
            insideWeather.text = insideWeatherText
        }
    }

    private fun updateOutsideWeather() {
        WeatherUtils().sendOutsideWeatherRequest(this) { currentWeatherResponse, currentCity ->
            val instantSunrise = Instant.ofEpochSecond(currentWeatherResponse.sys.sunrise.toLong())
                .atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneOffset.ofHours(5))
            val instantSunset = Instant.ofEpochSecond(currentWeatherResponse.sys.sunset.toLong())
                .atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneOffset.ofHours(5))
            val outsideWeatherText =
                "На улице (${currentCity.replaceFirstChar { it.uppercaseChar() }})<br>" +
                        "<br>" +
                        "температура:&nbsp;&nbsp;" +
                        "<font color=\"#FF0000\">${currentWeatherResponse.main.temp.let { if (it.toString().length < 5) it.toString() + "0" else it }} ℃</font><br>" +

                        "ощущается:&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "<font color=\"#FF0000\">${currentWeatherResponse.main.feels_like.let { if (it.toString().length < 5) it.toString() + "0" else it }} ℃</font><br>" +

                        "влажность:&nbsp;&nbsp;&nbsp;&nbsp;<font color=\"#5D4BD8\">${currentWeatherResponse.main.humidity}%</font><br>" +
                        "<br>" +
                        "скор. ветра:&nbsp;&nbsp;${currentWeatherResponse.wind.speed} м/с<br>" +
                        "направ. ветра:&nbsp;${WeatherUtils.getWindDirection(currentWeatherResponse.wind.deg)}°<br>" +
                        "<br>" +

                        "рассвет:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "${instantSunrise.hour.let { if (it < 10) "0$it" else it }}:${instantSunrise.minute.let { if (it < 10) "0$it" else it }}<br>" +

                        "закат:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "${instantSunset.hour.let { if (it < 10) "0$it" else it }}:${instantSunset.minute.let { if (it < 10) "0$it" else it }}<br>" +
                        "<br>" +
                        "давление:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${(currentWeatherResponse.main.pressure * 0.7501).roundToInt()}<br>" +
                        "видимость:&nbsp;&nbsp;&nbsp;&nbsp;${currentWeatherResponse.visibility} м<br>" +
                        "${
                            if (currentWeatherResponse.rain?.`1h` != null) {
                                "дождь за час: ${currentWeatherResponse.rain.`1h`} мм<br>"
                            } else {
                                ""
                            }
                        }" +
                        "${
                            if (currentWeatherResponse.snow?.`1h` != null) {
                                "снег за час:  ${currentWeatherResponse.snow.`1h`} мм"
                            } else {
                                ""
                            }
                        }"
            outsideWeather.text = Html.fromHtml(outsideWeatherText, FROM_HTML_OPTION_USE_CSS_COLORS)
            weatherIconDescription.text =
                currentWeatherResponse.weather.first().description.replaceFirstChar { it.uppercaseChar() }

            weatherIcon.setImageResource(
                this.resources.getIdentifier(
                    "_" + currentWeatherResponse.weather.first().icon,
                    "drawable",
                    this.packageName
                )
            )
//            temperatureBar.progress = currentWeatherResponse.main.temp.toInt()
//            humidityBar.progress = currentWeatherResponse.main.humidity
        }

    }
}