package hu.bme.ewaste.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.objects.DetectedObject
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.ewaste.service.TrashCanTracker
import hu.bme.ewaste.util.TrashCanObjectDetector
import javax.inject.Inject

typealias DetectedObjects = List<DetectedObject>

@HiltViewModel
class TrashCanViewModel @Inject constructor(
    val trashCanObjectDetector: TrashCanObjectDetector,
    trashCanTracker: TrashCanTracker
): ViewModel(), Observer<DetectedObjects> {

    private var _detectedObjects: MutableLiveData<DetectedObjects> = MutableLiveData()
    val detectedObjects: LiveData<DetectedObjects>
        get() = _detectedObjects

    init {
        trashCanObjectDetector.registerObserver(this)
        trashCanObjectDetector.registerObserver(trashCanTracker)
    }

    override fun onChanged(t: List<DetectedObject>) {
        _detectedObjects.value = t
    }

    override fun onCleared() {
        super.onCleared()
        trashCanObjectDetector.urRegisterObserver(this)
    }

}