package ru.netology.maps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.awaitAnimateCamera
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.cameraPosition
import com.google.maps.android.ktx.utils.collection.addMarker
import kotlinx.android.synthetic.main.markers_list_card.*
import kotlinx.coroutines.flow.collect
import ru.netology.maps.R
import ru.netology.maps.dto.Marker
import ru.netology.maps.viewModel.MarkerViewModel

class MapsFragment : Fragment() {
    private lateinit var googleMap: GoogleMap

    private val viewModel: MarkerViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                Toast.makeText(requireContext(), R.string.permission_unavailable, Toast.LENGTH_LONG)
                    .show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveFab = view.findViewById<View>(R.id.save_fab)
        val collectionFab = view.findViewById<View>(R.id.collection_fab)


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        lifecycle.coroutineScope.launchWhenCreated {
            googleMap = mapFragment.awaitMap().apply {
                isTrafficEnabled = true
                isBuildingsEnabled = true

                uiSettings.apply {
                    isZoomControlsEnabled = true
                    setAllGesturesEnabled(true)
                }
            }

            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    googleMap.apply {
                        isMyLocationEnabled = true
                        uiSettings.isMyLocationButtonEnabled = true
                    }

                    val fusedLocationProviderClient = LocationServices
                        .getFusedLocationProviderClient(requireActivity())

                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        println(it)
                    }
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    Toast.makeText(requireContext(), R.string.permission_requied, Toast.LENGTH_LONG)
                        .show()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            val target = LatLng(55.751999, 37.617734)
            val userTarget = arguments?.coordsArg?.let { LatLng(it.first(), it.last()) }
            val markerManager = MarkerManager(googleMap)
            val collection: MarkerManager.Collection = markerManager.newCollection()

            collection.setOnMarkerClickListener { marker ->
                marker.showInfoWindow()
                true
            }

            collection.setOnInfoWindowLongClickListener { marker ->
                val tag = marker.tag as Marker
                val dialog = EditMarkerFragment()
                dialog.arguments = Bundle().apply {
                        editArg = arrayListOf(
                            getString(R.string.new_marker_title),
                            marker.title
                        )
                        markerArg = tag
                        viewModel.edit(tag)
                    }
                dialog.show(childFragmentManager, "EditMarkerFragment")
            }

            googleMap.setOnMapLongClickListener { marker ->
                addMarker(collection, marker, getString(R.string.want_to_visit))
                saveFab.visibility = View.VISIBLE

                saveFab.setOnClickListener {
                    val dialog = AddMarkerFragment()
                    dialog.arguments = Bundle().apply {
                        markerArg = Marker(
                            title = getString(R.string.want_to_visit),
                            latitude = marker.latitude,
                            longitude = marker.longitude
                        )
                    }
                    dialog.show(childFragmentManager, "AddMarkerFragment")
                }
            }

            collectionFab.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mapsFragment_to_markersListFragment
                )
            }

            googleMap.awaitAnimateCamera(
                CameraUpdateFactory.newCameraPosition(
                    cameraPosition {
                        if (userTarget != null) {
                            target(userTarget)
                        } else {
                            target(target)
                        }
                        zoom(15F)
                    }
                )
            )

            viewModel.data.collect { data ->
                try {
                    collection.clear()
                    data.forEach { marker ->
                        addMarker(
                            collection,
                            LatLng(marker.latitude, marker.longitude),
                            marker.title
                        )
                        collection.markers.map {
                            if (it.tag == null) it.tag = marker
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    viewModel.loadMarkerExceptionEvent.observe(viewLifecycleOwner, {
                        val activity = activity ?: return@observe
                        val dialog = activity.let { activity ->
                            AlertDialog.Builder(activity)
                        }

                        dialog.setMessage(R.string.error_loading)
                            .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.dialog_negative_button) { dialog, _ ->
                                dialog.cancel()
                            }
                            .create()
                            .show()
                    })
                }
            }
        }
    }

    companion object {
        private const val MARKER_KEY = "MARKER_KEY"
        var Bundle.markerArg: Marker?
            set(value) = putParcelable(MARKER_KEY, value)
            get() = getParcelable(MARKER_KEY)

        private const val EDIT_KEY = "EDIT_KEY"
        var Bundle.editArg: ArrayList<String>?
            set(value) = putStringArrayList(EDIT_KEY, value)
            get() = getStringArrayList(EDIT_KEY)

        private const val COORDS_KEY = "COORDS_KEY"
        var Bundle.coordsArg: DoubleArray?
            set(value) = putDoubleArray(COORDS_KEY, value)
            get() = getDoubleArray(COORDS_KEY)
    }
}

private fun addMarker(collection: MarkerManager.Collection, latLng: LatLng, title: String) {
    collection.addMarker {
        position(latLng)
        icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
        title(title)
        snippet("$latLng")
    }
}