package hu.bme.ewaste.service

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.mlkit.vision.objects.DetectedObject
import timber.log.Timber
import javax.inject.Inject

typealias DetectedObjects = List<DetectedObject>

class TrashCanTracker @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) : Observer<DetectedObjects> {
    override fun onChanged(detectedObjects: DetectedObjects) {
        trackDetectedObjects(detectedObjects)
    }

    private fun trackDetectedObjects(detectedObjects: List<DetectedObject>) {
        detectedObjects.forEach {
            if (isNewObject(it)) {
                rememberNewObject(it)
                sendNewObject(it)
            }
        }
    }

    private fun isNewObject(detectedObject: DetectedObject): Boolean {
        return true // TODO
    }

    private fun rememberNewObject(detectedObject: DetectedObject) {

    }

    private fun sendNewObject(detectedObject: DetectedObject) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    Timber.d(it.toString())
                }
        } catch (e: SecurityException) {
        }
    }
}