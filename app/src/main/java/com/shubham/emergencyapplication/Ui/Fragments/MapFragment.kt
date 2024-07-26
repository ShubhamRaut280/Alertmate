package com.shubham.emergencyapplication.Ui.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.FamilyLocation
import com.shubham.emergencyapplication.Repositories.UserRepository.getFamilyMembersList
import com.shubham.emergencyapplication.Repositories.UserRepository.getLocationOnce
import com.shubham.emergencyapplication.databinding.FragmentMapBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var mapViewModel: MapViewModel
    private val markers = mutableMapOf<String, Marker>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        init()
    }

    private fun init() {

        fetchFamilyMembersAndLocations()
           mapViewModel.locations.observe(viewLifecycleOwner) { locations ->
               Log.d("MapFragment", "Locations updated: $locations")
            if (!locations.isNullOrEmpty()) {
                updateMarkers(locations)
            }
        }

    }

    private fun fetchFamilyMembersAndLocations() {
        getFamilyMembersList(requireContext(), object : ResponseCallBack<MutableList<String>> {
            override fun onSuccess(response: MutableList<String>?) {
                if (response != null) {
                    response.add(FirebaseAuth.getInstance().currentUser?.uid ?: return)
                    val locationsList = mutableListOf<FamilyLocation>()
                    var completedRequests = 0
                    response.forEach { id ->
                        getLocationOnce(requireContext(), id, object : ResponseCallBack<FamilyLocation> {
                            override fun onSuccess(res: FamilyLocation?) {
                                completedRequests++
                                res?.let {
                                    locationsList.add(it)
                                }
                                if (completedRequests == response.size) {
                                    mapViewModel.setLocations(locationsList)
                                }
                            }

                            override fun onError(error: String?) {
                                completedRequests++
                                Log.d("MapFragment", "getLocation error: $error")
                                if (completedRequests == response.size) {
                                    mapViewModel.setLocations(locationsList)
                                }
                            }
                        })
                    }
                }
            }

            override fun onError(error: String?) {
                Log.d("MapFragment", "getFamilyMembersList error: $error")
            }
        })
    }

    private fun updateMarkers(locations: List<FamilyLocation>) {
        googleMap.clear() // Clear previous markers
        locations.forEach { location ->
            Log.d("MapFragment", "Adding Location Marker: ${location.name} at ${location.latitude}, ${location.longitude}")
            val latLng = LatLng(location.latitude, location.longitude)
            val marker = markers[location.name]
            if (marker == null) {
                // Add new marker
                markers[location.name] = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(location.name)
                        .icon(getMarkerColor(location.timestamp))
                )!!
                Log.d("MapFragment", "Marker added for ${location.name}")
            } else {
                // Update existing marker position
                marker.position = latLng
                Log.d("MapFragment", "Marker updated for ${location.name}")
            }
        }
        // Adjust camera to show all markers
        if (locations.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            locations.forEach { location ->
                boundsBuilder.include(LatLng(location.latitude, location.longitude))
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
            Log.d("MapFragment", "Camera adjusted to fit all markers")
        }
    }

    private fun getMarkerColor(time: String): BitmapDescriptor {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS", Locale.getDefault())
        return try {
            val locationTime = dateFormat.parse(time)
            val currentTime = Date()
            val minutesDifference = locationTime?.let {
                TimeUnit.MILLISECONDS.toMinutes(currentTime.time - it.time)
            } ?: 0
            if (minutesDifference <= 1) {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            } else {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                googleMap.isMyLocationEnabled = true
            }
        }
    }
    private fun zoomToMarkers() {
        if (markers.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            markers.values.forEach { marker ->
                boundsBuilder.include(marker.position)
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
            Log.d("MapFragment", "Camera zoomed to fit all markers")
        }
    }
    override fun onResume() {
        super.onResume()

        binding.mapView.onResume()
        zoomToMarkers()

    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}
