package com.example.mystoryapp.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.response.ListStoryItem
import com.example.mystoryapp.data.showToast
import com.example.mystoryapp.databinding.FragmentMapsBinding
import com.example.mystoryapp.myadapter.MapsAdapter
import com.example.mystoryapp.ui.InfoStoryActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class FragmentMaps : Fragment() {
    private lateinit var binding: FragmentMapsBinding

    private val mapsViewModel: MapsViewModel by viewModels {
        MapsViewModel.MapsViewModelFactory.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.layoutMaps) as SupportMapFragment?
        mapFragment?.getMapAsync(onMapReady)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("PotentialBehaviorOverride")
    private val onMapReady = OnMapReadyCallback { googleMap ->
        googleMap.setInfoWindowAdapter(MapsAdapter(requireActivity()))
        googleMap.setOnInfoWindowClickListener {
            val story = it.tag as ListStoryItem
            val intent = Intent(requireContext(), InfoStoryActivity::class.java)
            intent.putExtra(InfoStoryActivity.DETAIL_STORY_EXTRA, story)
            startActivity(intent)
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-6.19, 106.61), 5f))
        setMapStyle(googleMap)

        showMapsStories(googleMap)
    }

    private fun showMapsStories(googleMap: GoogleMap) {
        mapsViewModel.getToken().observe(this) { token ->
            if (token != null) {
                val tokenUser = "Bearer $token"
                mapsViewModel.getStories(tokenUser).observe(this) {
                    when (it) {
                        is Result.Loading -> {
                            binding.loadingBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.loadingBar.visibility = View.GONE
                            initMarkers(googleMap, it.data)
                        }
                        is Result.Error -> {
                            binding.loadingBar.visibility = View.GONE
                            showToast(
                                requireContext(), getString(R.string.failed_map)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initMarkers(googleMap: GoogleMap, storyUser: List<ListStoryItem>) {
        storyUser.forEach {
            val marker = MarkerOptions()
            marker.position(LatLng(it.lat, it.lon))
                .title(it.name)
                .snippet(it.description)
            val addMarker = googleMap.addMarker(marker)
            addMarker?.tag = it
        }
    }

    private fun setMapStyle(mMap: GoogleMap) {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.maps_style
                    )
                )
            if (!success) {
                showToast(requireContext(), getString(R.string.failed_map))
            }
        } catch (exception: Resources.NotFoundException) {
            showToast(requireContext(), getString(R.string.failed_map))
        }
    }
}