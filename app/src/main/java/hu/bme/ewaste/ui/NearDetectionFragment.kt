package hu.bme.ewaste.ui

import android.os.Bundle
import android.view.View
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.accompanist.appcompattheme.AppCompatTheme
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.ewaste.R
import hu.bme.ewaste.databinding.FragmentNearDetectionsBinding

@AndroidEntryPoint
class NearDetectionFragment : Fragment(R.layout.fragment_near_detections) {

    private val viewModel by viewModels<NearDetectionsViewModel>()

    private lateinit var binding: FragmentNearDetectionsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNearDetectionsBinding.bind(view)


        viewModel.nearDetections.observe(viewLifecycleOwner) {
            binding.text.text = it.toString()
        }
        
        binding.nearDetectionsList.setContent {
            AppCompatTheme{
                NearDetectionList()
            }
        }
    }

    @Composable
    private fun NearDetectionList() {
        Text(text = "Test")
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
