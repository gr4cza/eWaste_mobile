package hu.bme.ewaste.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.ewaste.R

@AndroidEntryPoint
class NearDetectionFragment : Fragment(R.layout.fragment_near_detections) {

    private val viewModel by viewModels<NearDetectionsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.toString()
    }

}