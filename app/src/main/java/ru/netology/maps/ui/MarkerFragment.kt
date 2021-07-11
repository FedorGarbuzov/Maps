package ru.netology.maps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.maps.R
import ru.netology.maps.databinding.FragmentMarkerBinding
import ru.netology.maps.dto.Marker
import ru.netology.maps.ui.MapsFragment.Companion.editArg
import ru.netology.maps.ui.MapsFragment.Companion.markerArg
import ru.netology.maps.viewModel.MarkerViewModel

class MarkerFragment : Fragment() {

    private val viewModel: MarkerViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        val binding = FragmentMarkerBinding.inflate(inflater, container, false)

        arguments?.markerArg?.let { marker ->
            with(binding) {
                titleText.text = marker.title
                latitudeText.text = marker.latitude.toString()
                longitudeText.text = marker.longitude.toString()

                editTitle.setOnClickListener {
                    showDialog(
                        getString(R.string.new_marker_title),
                        marker.title,
                        marker
                    )
                }
                editLatitudeText.setOnClickListener {
                    showDialog(
                        getString(R.string.latitude),
                        marker.latitude.toString(),
                        marker
                    )
                }
                editLongitudeText.setOnClickListener {
                    showDialog(
                        getString(R.string.longitude),
                        marker.longitude.toString(),
                        marker
                    )
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun showDialog(claimHelp: String, claimText: String, marker: Marker) {
        val dialog = EditMarkerFragment()
        dialog.arguments = Bundle().apply {
            editArg = arrayListOf(claimHelp, claimText)
            markerArg = marker
            viewModel.edit(marker)
        }
        dialog.show(parentFragmentManager, "EditClaimFragment")
    }
}