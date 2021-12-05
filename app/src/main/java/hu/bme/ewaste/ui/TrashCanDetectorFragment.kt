package hu.bme.ewaste.ui

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.mediapipe.components.CameraHelper
import com.google.mediapipe.components.CameraXPreviewHelper
import com.google.mediapipe.components.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.ewaste.R
import hu.bme.ewaste.databinding.FragmentTrashCanDetectorBinding
import hu.bme.ewaste.detector.TrashCanObjectDetector
import javax.inject.Inject

@AndroidEntryPoint
class TrashCanDetectorFragment : Fragment(R.layout.fragment_trash_can_detector) {

    private val viewModel by viewModels<TrashCanDetectorViewModel>()

    private lateinit var binding: FragmentTrashCanDetectorBinding

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private lateinit var previewFrameTexture: SurfaceTexture

    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private lateinit var previewDisplayView: SurfaceView

    @Inject
    lateinit var trashCanObjectDetector: TrashCanObjectDetector

    // Handles camera access via the {@link CameraX} Jetpack support library.
    private lateinit var cameraHelper: CameraXPreviewHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrashCanDetectorBinding.bind(view)

        previewDisplayView = SurfaceView(requireContext())
        setupPreviewDisplayView()

        viewModel.isTracking.observe(viewLifecycleOwner) { tracking ->
            binding.btnTracking.text = if (tracking) {
                getString(R.string.stop_tracking)
            } else {
                getString(R.string.start_tracking)
            }
        }

        binding.btnTracking.setOnClickListener {
            viewModel.toggleTracking()
        }
    }

    private fun setupPreviewDisplayView() {
        previewDisplayView.visibility = View.GONE
        val viewGroup = binding.previewDisplayLayout
        viewGroup.addView(previewDisplayView)
        previewDisplayView
            .holder
            .addCallback(
                object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        trashCanObjectDetector.setSurface(holder)
                    }

                    override fun surfaceChanged(
                        holder: SurfaceHolder,
                        format: Int,
                        width: Int,
                        height: Int
                    ) {
                        onPreviewDisplaySurfaceChanged(width, height)
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        trashCanObjectDetector.setSurface(null)
                    }
                })
    }

    private fun onPreviewDisplaySurfaceChanged(
        width: Int, height: Int
    ) {
        // (Re-)Compute the ideal size of the camera-preview display (the area that the
        // camera-preview frames get rendered onto, potentially with scaling and rotation)
        // based on the size of the SurfaceView that contains the display.
        val viewSize = Size(width, height)
        val displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize)
        val isCameraRotated = cameraHelper.isCameraRotated

        // Connect the converter to the camera-preview frames as its input (via
        // previewFrameTexture), and configure the output width and height as the computed
        // display size.
        if (displaySize != null) {
            trashCanObjectDetector.setSurfaceTexture(
                previewFrameTexture,
                if (isCameraRotated) displaySize.height else displaySize.width,
                if (isCameraRotated) displaySize.width else displaySize.height
            )
        }
    }

    override fun onResume() {
        super.onResume()
        trashCanObjectDetector.startDetection()

        if (PermissionHelper.cameraPermissionsGranted(requireActivity())) {
            startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        trashCanObjectDetector.stopDetection()
    }

    private fun startCamera() {
        cameraHelper = CameraXPreviewHelper()
        cameraHelper.setOnCameraStartedListener { surfaceTexture ->
            if (surfaceTexture != null) {
                previewFrameTexture = surfaceTexture
            }
            // Make the display view visible to start showing the preview. This triggers the
            // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
            previewDisplayView.visibility = View.VISIBLE
        }
        cameraHelper.startCamera(requireActivity(), CAMERA_FACING,  /*surfaceTexture=*/null)
    }

    companion object {
        private val CAMERA_FACING = CameraHelper.CameraFacing.BACK
    }
}