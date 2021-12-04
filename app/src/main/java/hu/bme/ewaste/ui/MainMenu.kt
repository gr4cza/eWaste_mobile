package hu.bme.ewaste.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import hu.bme.ewaste.R
import hu.bme.ewaste.databinding.FragmentMainMenuBinding


class MainMenu : Fragment(R.layout.fragment_main_menu) {

    private lateinit var binding: FragmentMainMenuBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainMenuBinding.bind(view)

        binding.detectionButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainMenu_to_trashCanDetector)
        }
    }
}