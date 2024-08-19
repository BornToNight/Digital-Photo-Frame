package ru.borntonight.digitalphotoframe.activity

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.Intent.ACTION_OPEN_DOCUMENT_TREE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.gson.Gson
import ru.borntonight.digitalphotoframe.R
import ru.borntonight.digitalphotoframe.dto.Geolocation
import ru.borntonight.digitalphotoframe.util.AppConstants.CITY_GEOLOCATION_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_SLIDE_SHOW_DELAY
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_UPDATE_WEATHER_DELAY
import ru.borntonight.digitalphotoframe.util.AppConstants.GEOLOCATION_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.SHARED_PREF_VALUE
import ru.borntonight.digitalphotoframe.util.AppConstants.SHUFFLE_PHOTO_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.SLIDE_SHOW_DELAY_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.UPDATE_WEATHER_DELAY_KEY
import ru.borntonight.digitalphotoframe.util.CityGeolocationEnum
import ru.borntonight.digitalphotoframe.util.GeolocationUtils

class MainActivity : AppCompatActivity() {

    private lateinit var photoImage: ImageView
    private lateinit var weatherImage: ImageView
    private lateinit var settingsImage: ImageView
    private lateinit var photoText: TextView
    private lateinit var weatherText: TextView
    private lateinit var settingsText: TextView
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)
        window.insetsController?.hide(WindowInsets.Type.navigationBars())

        photoImage = findViewById(R.id.photoImage)
        photoImage.setOnClickListener {
            val intent = Intent(this, SlideShowActivity::class.java)
            startActivity(intent)
        }
        photoText = findViewById(R.id.photoText)
        photoText.setOnClickListener {
            val intent = Intent(this, SlideShowActivity::class.java)
            startActivity(intent)
        }

        weatherImage = findViewById(R.id.weatherImage)
        weatherImage.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
        }
        weatherText = findViewById(R.id.weatherText)
        weatherText.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
        }

        settingsImage = findViewById(R.id.settingsImage)
        settingsImage.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        settingsText = findViewById(R.id.settingsText)
        settingsText.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        mFusedLocationClient = getFusedLocationProviderClient(this)

        registerPermissions()
        checkIsGeolocationExsists()
        settingSlideShowDelay()
        settingUpdateWeatherDelay()
        settingCity()
        settingShufflePhoto()
    }

    private fun checkIsGeolocationExsists() {
        if (!getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).contains(
                GEOLOCATION_KEY
            )
        ) {
            GeolocationUtils().getLocation(this, false)
        }
    }

    private fun settingSlideShowDelay() {
        if (!getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).contains(
                SLIDE_SHOW_DELAY_KEY
            )
        ) {
            getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).edit().putLong(
                SLIDE_SHOW_DELAY_KEY,
                DEFAULT_SLIDE_SHOW_DELAY
            ).apply()
        }
    }

    private fun settingUpdateWeatherDelay() {
        if (!getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).contains(
                UPDATE_WEATHER_DELAY_KEY
            )
        ) {
            getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).edit().putLong(
                UPDATE_WEATHER_DELAY_KEY,
                DEFAULT_UPDATE_WEATHER_DELAY
            ).apply()
        }
    }

    private fun settingCity() {
        if (!getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).contains(
                CITY_GEOLOCATION_KEY
            )
        ) {
            getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).edit().putString(
                CITY_GEOLOCATION_KEY,
                Gson().toJson(CityGeolocationEnum.PERM)
            ).apply()
        }
    }

    private fun settingShufflePhoto() {
        if (!getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).contains(
                SHUFFLE_PHOTO_KEY
            )
        ) {
            getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).edit().putBoolean(
                SHUFFLE_PHOTO_KEY,
                true
            ).apply()
        }
    }

    private fun registerPermissions() {

        val requestPermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(
                arrayOf(
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_VISUAL_USER_SELECTED,
                    ACTION_OPEN_DOCUMENT_TREE,
                    ACTION_OPEN_DOCUMENT,
                    READ_EXTERNAL_STORAGE,
                    ACTION_GET_CONTENT,
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                    INTERNET,
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
    }
}