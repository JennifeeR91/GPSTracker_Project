package com.gpstracker.gpstracker_project.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.result_activity.*

class ResultActivityDetail : AppCompatActivity() , OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity_detail)
        // page Title
        tvPageTitle.text = "Activity Detail"

        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map1) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)

    }
    override fun onMapReady(googleMap: GoogleMap?) {


        val latLngStart = LatLng(47.0, 33.4)
        val latLngEnd = LatLng(47.2, 33.4)

        val startMarker = MarkerOptions().position(latLngStart).title("Startpoint")
        val endMarker = MarkerOptions().position(latLngEnd).title("Endpoint")


        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 15F))

        // add marker
        googleMap?.addMarker(startMarker)
        googleMap?.addMarker(endMarker)
    }

    }