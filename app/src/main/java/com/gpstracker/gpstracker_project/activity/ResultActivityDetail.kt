package com.gpstracker.gpstracker_project.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.gpstracker.gpstracker_project.R
import com.gpstracker.gpstracker_project.data
import kotlinx.android.synthetic.main.result_activity.*
import java.util.concurrent.TimeUnit

class ResultActivityDetail: AppCompatActivity() , OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity_detail)
        // page Title
        tvPageTitle.text = "Activity Detail"


        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map_result) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap?) {



        val latLngStart = LatLng(47.0, 33.0)
        val latLngEnd = LatLng(47.1, 33.0)

        val startMarker = MarkerOptions().position(latLngStart).title("Startpoint")
        val endMarker = MarkerOptions().position(latLngEnd).title("Endpoint")

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 15F))

        // add marker
        googleMap?.addMarker(startMarker)
        googleMap?.addMarker(endMarker)

        // mir schleife durch alle punkte durchgehen und sie hinzufügen
        // Adding points to ArrayList
        val coordList = ArrayList<LatLng>()


        // additional testpoint
        coordList.add(LatLng(47.093, 15.436))



    }
}






    // show hh:mm:ss from timestamp difference
    public fun getDuration(start: Long, end: Long): String {
        // get difference and show result in h:m:s
        val diffTime = end.toLong() - start.toLong()
        val periodAsHH_MM_SS = java.lang.String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(diffTime),
            TimeUnit.MILLISECONDS.toMinutes(diffTime) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(diffTime) % TimeUnit.MINUTES.toSeconds(1))
        return periodAsHH_MM_SS
}
