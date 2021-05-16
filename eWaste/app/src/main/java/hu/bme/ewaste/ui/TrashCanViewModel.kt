package hu.bme.ewaste.ui

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
    private val trashCanTracker: TrashCanTracker
) : ViewModel(), Observer<DetectedObjects> {

    private var _detectedObjects: MutableLiveData<DetectedObjects> = MutableLiveData()
    val detectedObjects: LiveData<DetectedObjects>
        get() = _detectedObjects

    private var _isTracking = MutableLiveData(false)
    val isTracking: LiveData<Boolean>
        get() = _isTracking

    init {
        trashCanObjectDetector.registerObserver(this)
    }

    fun toggleTracking() {
        if (_isTracking.value == false) {
            trashCanObjectDetector.registerObserver(trashCanTracker)
        } else {
            trashCanObjectDetector.urRegisterObserver(trashCanTracker)
        }
        _isTracking.value = _isTracking.value?.not()
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