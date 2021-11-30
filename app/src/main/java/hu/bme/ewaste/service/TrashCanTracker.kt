package hu.bme.ewaste.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import hu.bme.ewaste.data.dto.DetectionDTO
import hu.bme.ewaste.data.model.DetectedObject
import hu.bme.ewaste.data.model.TrackedObject
import hu.bme.ewaste.repository.TrashCanRepository
import hu.bme.ewaste.ui.DetectedObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val THRESHOLD = 10

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
            }
        }

        if (knownObjects.any { (_, value) -> value.detectionCount == THRESHOLD }) {
            sendNewObject(
                knownObjects.map {
                    it.value
                }.filter {
                    it.detectionCount >= THRESHOLD
                }
            )
        }
    }

    private fun sendNewObject(trackedObjects: List<TrackedObject>) {
        if (locationPermissionsGranted()) {
            MainScope().launch(Dispatchers.Default) {
                try {
                    val location: Location = getLocation()
                    trashCanRepository.sendDetectedTrashCans(
                        trackedObjects.map { trackedObject ->
                            DetectionDTO(
                                trackedObject.localId,
                                trackedObject.type,
                                hu.bme.ewaste.data.dto.Location(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        }
                    )
                } catch (e: SecurityException) {
                    // TODO
                }
            }
        }
    }

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

    private fun locationPermissionsGranted(): Boolean {
        LOCATION_PERMISSIONS.forEach {
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun startTracking() {
        cancellationTokenSource = CancellationTokenSource()
        isTracking = true
    }

    fun stopTracking() {
        isTracking = false
        cancellationTokenSource.cancel()
    }

    companion object {
        val LOCATION_PERMISSIONS = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
