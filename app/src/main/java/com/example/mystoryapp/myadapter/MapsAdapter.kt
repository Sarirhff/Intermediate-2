package com.example.mystoryapp.myadapter

import android.app.Activity
import android.view.View
import com.example.mystoryapp.R
import com.example.mystoryapp.databinding.ItemMapsBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MapsAdapter (private val context: Activity) :
    GoogleMap.InfoWindowAdapter {
    private val binding: ItemMapsBinding by lazy {
        ItemMapsBinding.inflate(context.layoutInflater)
    }

    override fun getInfoContents(marker: Marker): View {
        binding.tvMapsName.text = context.getString(R.string.maps_story_tittle).format(marker.title)
        binding.tvMapsDescription.text = marker.snippet

        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}