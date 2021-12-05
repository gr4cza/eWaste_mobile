package hu.bme.ewaste.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.ewaste.data.dto.DetectionResponse
import hu.bme.ewaste.data.dto.Location
import hu.bme.ewaste.repository.TrashCanRepository
import hu.bme.ewaste.util.LocationUtil.locationPermissionsGranted
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NearDetectionsViewModel @Inject constructor(
    private val trashCanRepository: TrashCanRepository,
    private val fusedLocationClient: FusedLocationProviderClient,
) : ViewModel() {

    val nearDetections = MutableLiveData<List<DetectionResponse>>()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.let {
                Timber.d(it.toString())
                viewModelScope.launch {
                    val nearDetectedTrashCans = trashCanRepository.getNearDetectedTrashCans(
                        Location(
                            it[0].latitude,
                            it[0].longitude
                        )
                    )
                    Timber.d(nearDetectedTrashCans.toString())
                    nearDetections.value = nearDetectedTrashCans
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startTracking(appContext: Context) {
        Timber.d("tracking started")
        if (locationPermissionsGranted(appContext)) {
            val request = com.google.android.gms.location.LocationRequest.create().apply {
                interval = 5000L
                fastestInterval = 2000L
                isWaitForAccurateLocation = true
                priority = PRIORITY_HIGH_ACCURACY
                smallestDisplacement = 3.0f
            }
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Timber.d("tracking stopped")
    }

}