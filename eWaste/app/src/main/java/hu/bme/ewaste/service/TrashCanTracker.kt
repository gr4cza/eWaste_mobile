package hu.bme.ewaste.service

import android.location.Location
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.mlkit.vision.objects.DetectedObject
import hu.bme.ewaste.repository.TrashCanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject

typealias DetectedObjects = List<DetectedObject>

class TrashCanTracker @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val trashCanRepository: TrashCanRepository
) : Observer<DetectedObjects> {

    private val knownObjects = mutableMapOf<Int, Int>()

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
        detectedObject.trackingId?.let {
            var isKnown = knownObjects.containsKey(it)
            knownObjects.putIfAbsent(it, 0)
            knownObjects[it]?.inc()
            return !isKnown
        }
        return false
    }

    private fun rememberNewObject(detectedObject: DetectedObject) {

    }

    private fun sendNewObject(detectedObject: DetectedObject) {
        MainScope().launch(Dispatchers.Default) {
            try {
                val location: Location = fusedLocationClient.lastLocation.await()
                val currentTime = Calendar.getInstance().time
                val type = detectedObject.labels.getOrNull(0)?.text ?: "Unknown"
                Timber.d("$location type: $type time: $currentTime")
                trashCanRepository.writeNewObject(type, location, currentTime)
            } catch (e: SecurityException) {
            }
        }
    }
}


