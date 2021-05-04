package hu.bme.ewaste.util

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.Observer
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import timber.log.Timber
import javax.inject.Inject

class TrashCanObjectDetector @Inject constructor() : ImageAnalysis.Analyzer {

    private var objectDetector: ObjectDetector

    private val observers: MutableList<Observer<List<DetectedObject>>> = mutableListOf()

    init {
        val localModel = LocalModel.Builder()
            .setAssetFilePath("test_model.tflite")
            .build()

        // Live detection and tracking
        val customObjectDetectorOptions =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(3)
                .build()

        objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)
    }

    fun registerObserver(observer: Observer<List<DetectedObject>>) {
        observers.add(observer)
    }

    private fun updateObservers(detectedObjects: List<DetectedObject>) {
        observers.forEach{
            it.onChanged(detectedObjects)
        }
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            objectDetector
                .process(image)
                .addOnSuccessListener { results ->

                    updateObservers(results)

                    for (detectedObject in results) {
                        Timber.d("analyze: ${detectedObject.boundingBox}")
                        detectedObject.labels.forEach {
                            Timber.d("analyze: ${it.text}")
                        }
                        Timber.d("id: ${detectedObject.trackingId}")
                    }
                    // TODO remove debugging
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

}