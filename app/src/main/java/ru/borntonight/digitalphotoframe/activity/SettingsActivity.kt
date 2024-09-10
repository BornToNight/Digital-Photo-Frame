package ru.borntonight.digitalphotoframe.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.gson.Gson
import ru.borntonight.digitalphotoframe.R
import ru.borntonight.digitalphotoframe.util.AppConstants.CITY_GEOLOCATION_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_PERCENT_BRIGHTNESS_OFF
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_PERCENT_BRIGHTNESS_ON
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_SLIDE_SHOW_DELAY
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_TIME_OFF
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_TIME_ON
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_UPDATE_WEATHER_DELAY
import ru.borntonight.digitalphotoframe.util.AppConstants.PERCENT_BRIGHTNESS_OFF_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.PERCENT_BRIGHTNESS_ON_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.SHARED_PREF_VALUE
import ru.borntonight.digitalphotoframe.util.AppConstants.SHUFFLE_PHOTO_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.SLIDE_SHOW_DELAY_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.TIME_OFF_SCREEN_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.TIME_ON_SCREEN_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.UPDATE_WEATHER_DELAY_KEY
import ru.borntonight.digitalphotoframe.util.CityGeolocationEnum
import ru.borntonight.digitalphotoframe.util.GeolocationUtils

class SettingsActivity : AppCompatActivity() {

    private lateinit var resetGpsImageView: ImageView
    private lateinit var chooseCitySpinner: Spinner
    private lateinit var slideShowDelayEditText: EditText
    private lateinit var updateWeatherDelayEditText: EditText
    private lateinit var updateTimeOffEditText: EditText
    private lateinit var updateTimeOnEditText: EditText
    private lateinit var updateBrightnessOffEditText: EditText
    private lateinit var updateBrightnessOnEditText: EditText
    private lateinit var saveSettingsButton: Button
    private lateinit var shufflePhotoCheckBox: CheckBox
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.insetsController?.hide(WindowInsets.Type.navigationBars())

        resetGpsImageView = findViewById(R.id.resetGpsImageView)
        chooseCitySpinner = findViewById(R.id.chooseCitySpinner)
        slideShowDelayEditText = findViewById(R.id.slideShowDelayEditText)
        updateWeatherDelayEditText = findViewById(R.id.updateWeatherDelayEditText)
        updateTimeOffEditText = findViewById(R.id.updateTimeOffEditText)
        updateTimeOnEditText = findViewById(R.id.updateTimeOnEditText)
        updateBrightnessOffEditText = findViewById(R.id.updateBrightnessOffEditText)
        updateBrightnessOnEditText = findViewById(R.id.updateBrightnessOnEditText)
        saveSettingsButton = findViewById(R.id.saveSettingsbutton)
        shufflePhotoCheckBox = findViewById(R.id.shufflePhotoCheckBox)

        mFusedLocationClient = getFusedLocationProviderClient(this)

        sharedPreferences = getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE)

        slideShowDelayEditText.setText(
            (sharedPreferences.getLong(
                SLIDE_SHOW_DELAY_KEY, DEFAULT_SLIDE_SHOW_DELAY
            ) / 1000).toString()
        )

        updateWeatherDelayEditText.setText(
            (sharedPreferences.getLong(
                UPDATE_WEATHER_DELAY_KEY, DEFAULT_UPDATE_WEATHER_DELAY
            ) / 60000).toString()
        )

        updateTimeOffEditText.setText(
            sharedPreferences.getString(
                TIME_OFF_SCREEN_KEY, DEFAULT_TIME_OFF
            )
        )

        updateTimeOnEditText.setText(
            sharedPreferences.getString(
                TIME_ON_SCREEN_KEY, DEFAULT_TIME_ON
            )
        )

        updateBrightnessOffEditText.setText(
            sharedPreferences.getInt(
                PERCENT_BRIGHTNESS_OFF_KEY, DEFAULT_PERCENT_BRIGHTNESS_OFF
            ).toString()
        )

        updateBrightnessOnEditText.setText(
            sharedPreferences.getInt(
                PERCENT_BRIGHTNESS_ON_KEY, DEFAULT_PERCENT_BRIGHTNESS_ON
            ).toString()
        )

        resetGpsImageView.setOnClickListener {
            GeolocationUtils().getLocation(this, true)

        }

        if (sharedPreferences.contains(CITY_GEOLOCATION_KEY)) {
            chooseCitySpinner.setSelection(
                Gson().fromJson(
                    sharedPreferences.getString(CITY_GEOLOCATION_KEY, ""),
                    CityGeolocationEnum::class.java
                ).id
            )
        }

        if (sharedPreferences.contains(SHUFFLE_PHOTO_KEY)) {
            shufflePhotoCheckBox.isChecked = sharedPreferences.getBoolean(SHUFFLE_PHOTO_KEY, true)
        }

        saveSettingsButton.setOnClickListener {
            sharedPreferences.edit().putLong(
                SLIDE_SHOW_DELAY_KEY, slideShowDelayEditText.text.toString().toLong() * 1000
            ).apply()
            sharedPreferences.edit().putLong(
                UPDATE_WEATHER_DELAY_KEY,
                updateWeatherDelayEditText.text.toString().toLong() * 60000
            ).apply()
            sharedPreferences.edit().putString(
                CITY_GEOLOCATION_KEY, Gson().toJson(
                    CityGeolocationEnum.getCityGeoEnumByCityName(chooseCitySpinner.selectedItem.toString())
                )
            ).apply()
            sharedPreferences.edit().putBoolean(
                SHUFFLE_PHOTO_KEY,
                shufflePhotoCheckBox.isChecked
            ).apply()
            sharedPreferences.edit().putString(
                TIME_OFF_SCREEN_KEY,
                updateTimeOffEditText.text.toString()
            ).apply()
            sharedPreferences.edit().putString(
                TIME_ON_SCREEN_KEY,
                updateTimeOnEditText.text.toString()
            ).apply()
            sharedPreferences.edit().putInt(
                PERCENT_BRIGHTNESS_OFF_KEY,
                updateBrightnessOffEditText.text.toString().toInt()
            ).apply()
            sharedPreferences.edit().putInt(
                PERCENT_BRIGHTNESS_ON_KEY,
                updateBrightnessOnEditText.text.toString().toInt()
            ).apply()
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return false
    }
}