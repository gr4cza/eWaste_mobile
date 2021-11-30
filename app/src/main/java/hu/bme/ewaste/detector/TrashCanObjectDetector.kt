package hu.bme.ewaste.detector

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.SurfaceHolder
import androidx.lifecycle.Observer
import com.google.mediapipe.components.ExternalTextureConverter
import com.google.mediapipe.components.FrameProcessor
import com.google.mediapipe.formats.proto.DetectionProto
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.glutil.EglManager
import hu.bme.ewaste.data.model.DetectedObject
import hu.bme.ewaste.data.model.TrashCanType
import hu.bme.ewaste.ui.DetectedObjects

class TrashCanObjectDetector(appContext: Context) {
    // Creates and manages an {@link EGLContext}.
    private var eglManager: EglManager = EglManager(null)

    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    private var processor: FrameProcessor

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private lateinit var converter: ExternalTextureConverter

    private val observers: MutableList<Observer<DetectedObjects>> = mutableListOf()

    init {
        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(appContext)

        eglManager = EglManager(null)
        processor = FrameProcessor(
            appContext,
            eglManager.nativeContext,
            BINARY_GRAPH_NAME,
            INPUT_VIDEO_STREAM_NAME,
            OUTPUT_VIDEO_STREAM_NAME
        )
        processor.videoSurfaceOutput.setFlipY(FLIP_FRAMES_VERTICALLY)

        processor.addPacketCallback(
            OUTPUT_TRACKED_DETECTIONS_STREAM_NAME
        ) { packet: Packet? ->
            val detectedObjects =
                PacketGetter.getProtoVector(
                    packet,
                    DetectionProto.Detection.parser()
                )
            detectObject(detectedObjects)
        }
    }

    private fun detectObject(detectedObjects: MutableList<DetectionProto.Detection>) {
        val detections = detectedObjects.map {
            val idx = 0
            DetectedObject(
                it.detectionId,
                TrashCanType.valueOf(it.getLabel(idx).uppercase()),
                it.getScore(idx)
            )
        }.toList()
        updateObservers(detections)
    }

    fun startDetection() {
        converter = ExternalTextureConverter(eglManager.context).apply {
            setFlipY(FLIP_FRAMES_VERTICALLY)
            setConsumer(processor)
        }
    }

    fun stopDetection() {
        converter.close()
    }

    fun setSurfaceTexture(previewFrameTexture: SurfaceTexture, width: Int, height: Int) {
        converter.setSurfaceTextureAndAttachToGLContext(
            previewFrameTexture,
            width,
            height
        )
    }

    fun setSurface(holder: SurfaceHolder?) {
        processor.videoSurfaceOutput.setSurface(holder?.surface)
    }

    fun registerObserver(observer: Observer<DetectedObjects>) {
        observers.add(observer)
    }

    fun unRegisterObserver(observer: Observer<DetectedObjects>) {
        observers.remove(observer)
    }

    private fun updateObservers(detectedObjects: DetectedObjects) {
        observers.forEach {
            it.onChanged(detectedObjects)
        }
    }

    companion object {
        init {
            // Load all native libraries needed by the app.
            System.loadLibrary("mediapipe_jni")
            System.loadLibrary("opencv_java3")
        }

        private const val BINARY_GRAPH_NAME = "mobile_gpu.binarypb"
        private const val INPUT_VIDEO_STREAM_NAME = "input_video"
        private const val OUTPUT_VIDEO_STREAM_NAME = "output_video"
        private const val OUTPUT_TRACKED_DETECTIONS_STREAM_NAME = "tracked_detections"

        // Flips the camera-preview frames vertically before sending them into FrameProcessor to be
        // processed in a MediaPipe graph, and flips the processed frames back when they are displayed.
        // This is needed because OpenGL represents images assuming the image origin is at the bottom-left
        // corner, whereas MediaPipe in general assumes the image origin is at top-left.
        private const val FLIP_FRAMES_VERTICALLY = true
    }
}