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

typealias DetectedObjects = List<DetectedObject>

class TrashCanObjectDetector : ImageAnalysis.Analyzer {

    private var objectDetector: ObjectDetector

    private val observers: MutableList<Observer<DetectedObjects>> = mutableListOf()

    private val trashNames =
        arrayListOf(
            "ashcan",
            "trash can",
            "garbage can",
            "wastebin",
            "ash bin",
            "ash-bin",
            "ashbin",
            "dustbin",
            "trash barrel",
            "trash bin"
        )

    init {
        Timber.d("ObjectDetector created")

        val localModel = LocalModel.Builder()
            .setAssetFilePath("efficientnet_lite4_int8_2.tflite")
            .build()

        // Live detection and tracking
        val customObjectDetectorOptions =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(2)
                .build()

        objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)
    }

    fun registerObserver(observer: Observer<DetectedObjects>) {
        observers.add(observer)
    }

    fun urRegisterObserver(observer: Observer<DetectedObjects>) {
        observers.remove(observer)
    }

    private fun updateObservers(detectedObjects: DetectedObjects) {
        observers.forEach {
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
                    val trashCans =
                        results.filter {
                            it.labels.any { label -> label.text in trashNames }
                        }

                    updateObservers(trashCans)

                    for (trashCan in trashCans) {
                        Timber.d("bounding box: ${trashCan.boundingBox}")
                        trashCan.labels.forEach {
                            Timber.d("labels: ${it.text}")
                        }
                        Timber.d("id: ${trashCan.trackingId}")
                    }
                    // TODO remove debugging
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

}