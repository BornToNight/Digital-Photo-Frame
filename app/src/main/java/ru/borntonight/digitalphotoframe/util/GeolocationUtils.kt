package ru.borntonight.digitalphotoframe.util

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.gson.Gson
import ru.borntonight.digitalphotoframe.dto.Geolocation
import ru.borntonight.digitalphotoframe.util.AppConstants.GEOLOCATION_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.SHARED_PREF_VALUE

class GeolocationUtils {
    @SuppressLint("ApplySharedPref")
    public fun getLocation(context: AppCompatActivity, resetGeo: Boolean) {

        if (ActivityCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            context.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            }.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                )
            )
        } else {
            if (resetGeo) {
                context.getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).edit().remove(
                    GEOLOCATION_KEY
                ).commit()
            }
            getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener(context) { location ->
                if (location != null) {
                    Toast.makeText(
                        context,
                        "Ваша геопозиция успешно обновлена",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    context.getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).edit()
                        .putString(
                            GEOLOCATION_KEY,
                            Gson().toJson(
                                Geolocation(
                                    location.latitude.toString(),
                                    location.longitude.toString()
                                )
                            ).toString()
                        ).apply()
                }
            }
        }
    }
}