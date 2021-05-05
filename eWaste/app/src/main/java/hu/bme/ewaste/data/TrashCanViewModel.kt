package hu.bme.ewaste.data

import androidx.lifecycle.*
import com.google.mlkit.vision.objects.DetectedObject
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.ewaste.service.TrashCanTracker
import hu.bme.ewaste.util.TrashCanObjectDetector
import kotlinx.coroutines.launch
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

    override fun onChanged(detectedObjects: DetectedObjects) {
        viewModelScope.launch {
            _detectedObjects.value = detectedObjects
        }
    }

    override fun onCleared() {
        super.onCleared()
        trashCanObjectDetector.urRegisterObserver(this)
    }

}