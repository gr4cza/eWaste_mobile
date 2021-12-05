package hu.bme.ewaste.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.accompanist.appcompattheme.AppCompatTheme
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.ewaste.R
import hu.bme.ewaste.databinding.FragmentNearDetectionsBinding
import hu.bme.ewaste.ui.components.DetectionCard

@AndroidEntryPoint
class NearDetectionFragment : Fragment(R.layout.fragment_near_detections) {

    private val viewModel by viewModels<NearDetectionsViewModel>()

    private lateinit var binding: FragmentNearDetectionsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNearDetectionsBinding.bind(view)

        binding.nearDetectionsList.setContent {
            AppCompatTheme {
                NearDetectionList()
            }
        }
    }

    @Composable
    private fun NearDetectionList() {
        val nearDetections by viewModel.nearDetections.observeAsState()
        nearDetections?.let {
            LazyColumn {
                items(it) { item ->
                    DetectionCard(detectionResponse = item, onClick = viewModel::emptyTrashCan)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startTracking(requireContext())
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopTracking()
    }

    @Preview
    @Composable
    fun ComposablePreview() {
        NearDetectionList()
    }
}
