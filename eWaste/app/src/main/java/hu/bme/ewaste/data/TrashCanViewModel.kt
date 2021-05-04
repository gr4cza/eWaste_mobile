package hu.bme.ewaste.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.objects.DetectedObject
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.ewaste.util.TrashCanObjectDetector
import javax.inject.Inject

typealias DetectedObjects = List<DetectedObject>

@HiltViewModel
class TrashCanViewModel @Inject constructor(
    val trashCanObjectDetector: TrashCanObjectDetector
): ViewModel(), Observer<DetectedObjects> {

    private var _detectedObjects: MutableLiveData<DetectedObjects> = MutableLiveData()
    val detectedObjects: LiveData<DetectedObjects>
        get() = _detectedObjects

    init {
        trashCanObjectDetector.registerObserver(this)
    }

    override fun onChanged(t: List<DetectedObject>) {
        _detectedObjects.value = t
    }

}