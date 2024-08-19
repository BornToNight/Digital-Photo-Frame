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
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_SLIDE_SHOW_DELAY
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_UPDATE_WEATHER_DELAY
import ru.borntonight.digitalphotoframe.util.AppConstants.SHARED_PREF_VALUE
import ru.borntonight.digitalphotoframe.util.AppConstants.SHUFFLE_PHOTO_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.SLIDE_SHOW_DELAY_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.UPDATE_WEATHER_DELAY_KEY
import ru.borntonight.digitalphotoframe.util.CityGeolocationEnum
import ru.borntonight.digitalphotoframe.util.GeolocationUtils

class SettingsActivity : AppCompatActivity() {

    private lateinit var resetGpsImageView: ImageView
    private lateinit var chooseCitySpinner: Spinner
    private lateinit var slideShowDelayEditText: EditText
    private lateinit var updateWeatherDelayEditText: EditText
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