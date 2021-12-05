package hu.bme.ewaste.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hu.bme.ewaste.repository.TrashCanRepository
import hu.bme.ewaste.service.TrashCanTracker
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NearDetectionsViewModel @Inject constructor(
    private val trashCanRepository: TrashCanRepository,
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext appContext: Context
) : ViewModel() {

    private var cancellationTokenSource: CancellationTokenSource = CancellationTokenSource()

    init {
        Timber.d("NearDetectionsViewModel started!")

        viewModelScope.launch {
            if (locationPermissionsGranted(appContext)) {
                while (isActive) {
                    val location = getLocation()
                    val nearDetectedTrashCans = trashCanRepository.getNearDetectedTrashCans(
                        location = hu.bme.ewaste.data.dto.Location(
                            location.latitude,
                            location.longitude
                        )
                    )
                    Timber.d(nearDetectedTrashCans.toString())
                    delay(5000L)
                }
            }
        }

    }

    @SuppressLint("MissingPermission", "InlinedApi")
    private suspend fun getLocation(): Location {
        return fusedLocationClient.getCurrentLocation(
            LocationRequest.QUALITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).await()
    }

    private fun locationPermissionsGranted(appContext: Context): Boolean {
        TrashCanTracker.LOCATION_PERMISSIONS.forEach {
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

}