package hu.bme.ewaste.service

import android.content.Context
import android.location.Location
import android.location.LocationRequest
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import hu.bme.ewaste.data.dto.DetectionDTO
import hu.bme.ewaste.data.model.DetectedObject
import hu.bme.ewaste.data.model.TrackedObject
import hu.bme.ewaste.repository.TrashCanRepository
import hu.bme.ewaste.ui.DetectedObjects
import hu.bme.ewaste.util.LocationUtil.locationPermissionsGranted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val THRESHOLD = 10
private const val PRECISION = 0.65

class TrashCanTracker @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val trashCanRepository: TrashCanRepository,
    private val appContext: Context
) : Observer<DetectedObjects> {

    private var knownObjects = HashMap<Long, TrackedObject>()

    private var isTracking = false

    private lateinit var cancellationTokenSource: CancellationTokenSource

    override fun onChanged(detectedObjects: DetectedObjects) {
        if (isTracking) {
            trackDetectedObjects(detectedObjects)
        }
    }

    private fun trackDetectedObjects(detectedObjects: List<DetectedObject>) {
        removeLostIds(detectedObjects)

        detectedObjects.forEach { detectedObject ->
            detectedObject.detectionId.let {
                val trackedObject = knownObjects.getOrPut(it) { TrackedObject(detectedObject) }
                trackedObject.detectionCount += 1
                if (trackedObject.precision < detectedObject.precision) {
                    trackedObject.precision = detectedObject.precision
                }
            }
        }

        if (knownObjects.any { (_, value) -> value.detectionCount == THRESHOLD }) {
            sendNewObject(
                knownObjects.map {
                    it.value
                }.filter {
                    it.detectionCount >= THRESHOLD && it.precision >= PRECISION
                }
            )
        }
    }

    private fun sendNewObject(trackedObjects: List<TrackedObject>) {
        if (locationPermissionsGranted(appContext)) {
            MainScope().launch(Dispatchers.IO) {
                try {
                    val location: Location = getLocation()
                    trashCanRepository.sendDetectedTrashCans(
                        trackedObjects.map { trackedObject ->
                            toDetectionDTO(trackedObject, location)
                        }
                    )
                } catch (e: SecurityException) {
                    // TODO
                }
            }
        }
    }

    private fun toDetectionDTO(
        trackedObject: TrackedObject,
        location: Location
    ) = DetectionDTO(
        trackedObject.localId,
        trackedObject.type,
        hu.bme.ewaste.data.dto.Location(
            location.latitude,
            location.longitude
        )
    )

    private fun removeLostIds(detectedObjects: List<DetectedObject>) {
        val detectedIds = detectedObjects.map { it.detectionId }
        knownObjects = knownObjects.filter { it.key in detectedIds }.toMap(HashMap())
    }

    private suspend fun getLocation(): Location {
        return fusedLocationClient.getCurrentLocation(
            LocationRequest.QUALITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).await()
    }

    fun startTracking() {
        cancellationTokenSource = CancellationTokenSource()
        isTracking = true
    }

    fun stopTracking() {
        isTracking = false
        cancellationTokenSource.cancel()
    }
}
