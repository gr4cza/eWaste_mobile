package hu.bme.ewaste.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.mediapipe.components.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.ewaste.R
import hu.bme.ewaste.databinding.ActivityMainBinding
import hu.bme.ewaste.service.TrashCanTracker.Companion.LOCATION_PERMISSIONS

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        if (savedInstanceState == null) {
            val fragment = TrashCanDetector()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_content, fragment)
                .commit()
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        (LOCATION_PERMISSIONS + CAMERA_PERMISSION).forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(it)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 10)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }
}