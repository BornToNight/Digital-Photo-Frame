package ru.borntonight.digitalphotoframe.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Html.FROM_HTML_OPTION_USE_CSS_COLORS
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
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


class SlideShowActivity : AppCompatActivity() {

    private lateinit var photoImageView: ImageView
    private lateinit var slideShowProgressBar: ProgressBar
    private lateinit var dateTextView: TextView
    private lateinit var temperatureInTextView: TextView
    private lateinit var temperatureOutTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var slideShowUtils: SlideShowUtils

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_show)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.insetsController?.hide(WindowInsets.Type.navigationBars())

        photoImageView = findViewById(R.id.photoImageView)
        slideShowProgressBar = findViewById(R.id.slideShowProgressBar)
        dateTextView = findViewById(R.id.dateTextView)
        temperatureInTextView = findViewById(R.id.temperatureInTextView)
        temperatureOutTextView = findViewById(R.id.temperatureOutTextView)

        sharedPreferences = getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE)

        val updateWeatherDelay = sharedPreferences.getLong(
            UPDATE_WEATHER_DELAY_KEY, DEFAULT_UPDATE_WEATHER_DELAY
        )

        lifecycleScope.launch {
            updateDateAndTime()
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

        slideShowUtils = SlideShowUtils()

        slideShowUtils.selectImageFolder(
            this,
            lifecycleScope,
            slideShowProgressBar,
            photoImageView
        )

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            slideShowUtils.stopSlideshow()
            finish()
        }
        return false
    }

    private fun updateDateAndTime() {
        dateTextView.text = DateAndTimeUtils().getDateAndTime()
    }

    private fun updateInsideWeather() {
        WeatherUtils().sendInsideWeatherRequest { temperatureResponse, _ ->
            if (temperatureResponse.isNotBlank()) {
                val temperatureInText =
                    "t in: <font color=\"#FF0000\">${temperatureResponse.let { if (it.toString().length < 5) it.toString() + "0" else it }} ℃</font>"
                temperatureInTextView.text =
                    Html.fromHtml(temperatureInText, FROM_HTML_OPTION_USE_CSS_COLORS)

            }

        }
    }

    private fun updateOutsideWeather() {
        WeatherUtils().sendOutsideWeatherRequest(this) { currentWeatherResponse, _ ->
            val temperatureOutText =
                "t out: <font color=\"#FF0000\">${currentWeatherResponse.main.temp.let { if (it.toString().length < 5) it.toString() + "0" else it }} ℃</font>"
            temperatureOutTextView.text =
                Html.fromHtml(temperatureOutText, FROM_HTML_OPTION_USE_CSS_COLORS)
        }

    }

}