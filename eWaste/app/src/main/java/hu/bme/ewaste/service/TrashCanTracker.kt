package hu.bme.ewaste.service

import androidx.lifecycle.Observer
import com.google.mlkit.vision.objects.DetectedObject

typealias DetectedObjects = List<DetectedObject>

class TrashCanTracker: Observer<DetectedObjects> {

    override fun onChanged(t: DetectedObjects?) {
        TODO("Not yet implemented")
    }
}