package hu.bme.ewaste.util

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import hu.bme.ewaste.databinding.ActivityMainBinding
import hu.bme.ewaste.ui.Draw
import timber.log.Timber

class TrashCanObjectDetector(private val context: Context, private val binding: ActivityMainBinding) :
    ImageAnalysis.Analyzer {

    private var objectDetector: ObjectDetector

    init {
        val localModel = LocalModel.Builder()
            .setAssetFilePath("lite-model_object_detection_mobile_object_labeler_v1_1.tflite")
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

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            objectDetector
                .process(image)
                .addOnSuccessListener { results ->
                    for (detectedObject in results) {
                        if (binding.root.childCount > 1) {
                            binding.root.removeViewAt(1)
                            val element = Draw(context, detectedObject.boundingBox, detectedObject.labels.firstOrNull()?.text ?: "Undefined")
                            binding.root.addView(element,1)
                        }
                        Timber.d( "analyze: ${detectedObject.boundingBox}")
                        detectedObject.labels.forEach { it ->
                            Timber.d( "analyze: ${it.text}")
                        }
                        Timber.d("id: ${detectedObject.trackingId}")
                    }
                    imageProxy.close()
                }
                .addOnFailureListener {
                    imageProxy.close()
                }
        }
    }

}