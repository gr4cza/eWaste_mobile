package hu.bme.ewaste.service

import android.location.Location
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.ewaste.model.DetectedObject
import hu.bme.ewaste.repository.TrashCanRepository
import hu.bme.ewaste.ui.DetectedObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val THRESHOLD = 10

class TrashCanTracker @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val trashCanRepository: TrashCanRepository
) : Observer<DetectedObjects> {

    private var knownObjects = HashMap<Long, Int>()

    private var isTracking = false
    lateinit var trackingSessionID: UUID

    override fun onChanged(detectedObjects: DetectedObjects) {
        if (isTracking) {
            trackDetectedObjects(detectedObjects)
        }
    }

    private fun trackDetectedObjects(detectedObjects: List<DetectedObject>) {
        removeLostIds(detectedObjects)

        detectedObjects.forEach { detectedObject ->
            detectedObject.trackingId.let {
                knownObjects.putIfAbsent(it, 0)
                knownObjects.computeIfPresent(it) { _, v -> v + 1 }
                if (knownObjects[it] == THRESHOLD) {
                    sendNewObject(detectedObject)
                }
            }
        }
    }

    private fun removeLostIds(detectedObjects: List<DetectedObject>) {
        val detectedIds = detectedObjects.map { it.trackingId }
        knownObjects = knownObjects.filter { it.key in detectedIds }.toMap(HashMap())
    }

    private fun sendNewObject(detectedObject: DetectedObject) {
        MainScope().launch(Dispatchers.Default) {
            try {
                val location: Location = fusedLocationClient.lastLocation.await()
                val currentTime = Calendar.getInstance().time
                val type = detectedObject.type.toString()
                Timber.d("$location type: $type time: $currentTime")
                trashCanRepository.writeNewObject(trackingSessionID, type, location, currentTime)
            } catch (e: SecurityException) {
            }
        }
    }

    fun startTracking() {
        isTracking = true
        trackingSessionID = UUID.randomUUID()
    }

    fun stopTracking() {
        isTracking = false
    }
}
