package hu.bme.ewaste

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class TrashCanObjectDetector: ImageAnalysis.Analyzer {

    private var objectDetector: ObjectDetector

    init {
        val localModel = LocalModel.Builder()
            .setAssetFilePath("model.tflite")
            .build()

        // Live detection and tracking
        val customObjectDetectorOptions =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
//                .setMaxPerObjectLabelCount(3)
                .build()

        objectDetector =  ObjectDetection.getClient(customObjectDetectorOptions)
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            objectDetector
                .process(image)
                .addOnSuccessListener { results ->
                    for (detectedObject in results) {
                        Log.d(TAG, "analyze: ${detectedObject.boundingBox}")
                    }
                }
        }

        imageProxy.close()
    }

    companion object{
        private const val TAG = "TrashCanObjectDetector"
    }
}