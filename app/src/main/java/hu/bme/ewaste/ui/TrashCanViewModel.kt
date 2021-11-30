package hu.bme.ewaste.ui

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.ewaste.detector.TrashCanObjectDetector
import hu.bme.ewaste.data.model.DetectedObject
import hu.bme.ewaste.service.TrashCanTracker
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias DetectedObjects = List<DetectedObject>

@HiltViewModel
class TrashCanViewModel @Inject constructor(
    private val trashCanObjectDetector: TrashCanObjectDetector,
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
        trashCanObjectDetector.registerObserver(trashCanTracker)
    }

    fun toggleTracking() {
        if (_isTracking.value == false) {
            trashCanTracker.startTracking()
        } else {
            trashCanTracker.stopTracking()
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
        trashCanObjectDetector.unRegisterObserver(this)
        trashCanObjectDetector.unRegisterObserver(trashCanTracker)
    }

}