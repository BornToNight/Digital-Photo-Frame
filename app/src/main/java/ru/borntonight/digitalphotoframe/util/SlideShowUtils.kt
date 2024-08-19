package ru.borntonight.digitalphotoframe.util

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.view.View.GONE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import ru.borntonight.digitalphotoframe.dto.Geolocation
import ru.borntonight.digitalphotoframe.util.AppConstants.DEFAULT_SLIDE_SHOW_DELAY
import ru.borntonight.digitalphotoframe.util.AppConstants.GEOLOCATION_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.SHARED_PREF_VALUE
import ru.borntonight.digitalphotoframe.util.AppConstants.SHUFFLE_PHOTO_KEY
import ru.borntonight.digitalphotoframe.util.AppConstants.SLIDE_SHOW_DELAY_KEY
import java.text.SimpleDateFormat
import java.util.Locale

class SlideShowUtils {

    var currentIndex = 0
    private var slideShowDelay = 0L
    private val handler = Handler(Looper.getMainLooper())

    public fun stopSlideshow() {
        handler.removeCallbacksAndMessages(null)
    }

    public fun selectImageFolder(
        context: AppCompatActivity,
        lifeCycleScope: CoroutineScope,
        photoProgressBar: ProgressBar,
        photoImageView: ImageView,
    ) {
        slideShowDelay =
            context.getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).getLong(
                SLIDE_SHOW_DELAY_KEY, DEFAULT_SLIDE_SHOW_DELAY
            )
        val resultLauncher =
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    if (uri != null) {
                        lifeCycleScope.launch {
                            loadImagesFromDirectory(context, photoProgressBar, uri, photoImageView)
                        }

                    }
                }
            }
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        resultLauncher.launch(intent)
    }

    private fun loadImagesFromDirectory(
        context: AppCompatActivity,
        photoProgressBar: ProgressBar,
        uri: Uri,
        photoImageView: ImageView,
    ) {
        val documentFile = DocumentFile.fromTreeUri(context, uri)
        val photoUriList = mutableListOf<Uri>()
        documentFile?.listFiles()?.forEach { file ->
            if (file.isFile && file.type?.startsWith("image") ?: false) {
                photoUriList.add(file.uri)
            }
        }

        if (context.getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).contains(
                SHUFFLE_PHOTO_KEY
            )
        ) {
            if (context.getSharedPreferences(SHARED_PREF_VALUE, Context.MODE_PRIVATE).getBoolean(
                    SHUFFLE_PHOTO_KEY, true
                )
            ) {
                photoUriList.shuffle()
            }
        }

        startSlideshow(photoUriList, photoProgressBar, photoImageView)
    }

    private fun startSlideshow(
        photoUriList: MutableList<Uri>,
        photoProgressBar: ProgressBar,
        photoImageView: ImageView,
    ) {
        if (photoUriList.isNotEmpty()) {
            handler.postDelayed(object : Runnable {
                override fun run() {
                    photoProgressBar.visibility = GONE
                    showImage(photoUriList, photoImageView)
                    handler.postDelayed(this, slideShowDelay)
                }
            }, slideShowDelay)
        }
    }

    private fun showImage(
        photoUriList: MutableList<Uri>,
        photoImageView: ImageView,
    ) {
        if (photoUriList.isNotEmpty()) {
            val imagePath = photoUriList[currentIndex]
            photoImageView.setImageURI(imagePath)
            currentIndex = (currentIndex + 1) % photoUriList.size
        }
    }

}