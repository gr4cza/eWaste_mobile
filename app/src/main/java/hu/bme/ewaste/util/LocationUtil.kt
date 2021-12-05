package hu.bme.ewaste.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object LocationUtil {
    val LOCATION_PERMISSIONS = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun locationPermissionsGranted(appContext: Context): Boolean {
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

}