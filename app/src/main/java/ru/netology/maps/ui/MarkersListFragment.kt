package ru.netology.maps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import ru.netology.maps.R
import ru.netology.maps.adapter.MarkerOnInteractionListener
import ru.netology.maps.adapter.MarkersListAdapter
import ru.netology.maps.databinding.FragmentMarkersListBinding
import ru.netology.maps.dto.Marker
import ru.netology.maps.ui.MapsFragment.Companion.coordsArg
import ru.netology.maps.ui.MapsFragment.Companion.markerArg
import ru.netology.maps.viewModel.MarkerViewModel

class MarkersListFragment : Fragment() {
    private val viewModel: MarkerViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentMarkersListBinding.inflate(inflater, container, false)

        val adapter = MarkersListAdapter(object : MarkerOnInteractionListener {
            override fun onShowMarker(marker: Marker) {
                findNavController().navigate(
                    R.id.action_markersListFragment_to_mapsFragment,
                    Bundle().apply {
                        coordsArg = doubleArrayOf(
                            marker.latitude,
                            marker.longitude
                        )
                    })
            }

            override fun onRemove(marker: Marker) {
                viewModel.removeById(marker.id)
            }

            override fun onEdit(marker: Marker) {
                findNavController().navigate(
                    R.id.action_markersListFragment_to_markerFragment,
                    Bundle().apply {
                        markerArg = marker
                        viewModel.edit(marker)
                    }
                )
            }
        })

        binding.addMarker.setOnClickListener {
            AddMarkerFragment().show(childFragmentManager, "AddMarkerFragment")
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenCreated {
            binding.makersList.adapter = adapter
            viewModel.data.collectLatest { data ->
                adapter.submitList(data)
            }
        }

        return binding.root
    }
}

