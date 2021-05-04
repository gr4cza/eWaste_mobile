package hu.bme.ewaste.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.objects.DetectedObject
import hu.bme.ewaste.util.TrashCanObjectDetector

typealias DetectedObjects = List<DetectedObject>

class TrashCanViewModel : ViewModel(), Observer<DetectedObjects> {
    private var _trashCanObjectDetector: TrashCanObjectDetector = TrashCanObjectDetector()
    val trashCanObjectDetector: TrashCanObjectDetector
        get() = _trashCanObjectDetector

    private var _detectedObjects: MutableLiveData<DetectedObjects> = MutableLiveData()
    val detectedObjects: LiveData<DetectedObjects>
        get() = _detectedObjects

    init {
        trashCanObjectDetector.registerObserver(this)
    }

    override fun onChanged(t: List<DetectedObject>?) {
        _detectedObjects.value = t
    }

}