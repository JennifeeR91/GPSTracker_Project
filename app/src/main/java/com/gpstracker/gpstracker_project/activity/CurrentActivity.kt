package com.gpstracker.gpstracker_project.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gpstracker.gpstracker_project.Preferences
import com.gpstracker.gpstracker_project.ActivityDataArrayHandler
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.current_activity.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


class CurrentActivity : AppCompatActivity(), OnMapReadyCallback {

   // private var googleMap: GoogleMap? = null
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionCode = 101

    // variable preferences instanziert Preferences
    //private val preferences = Preferences()

    //create array for data to save in
    //val data: MutableList<String> = ArrayList()
    private val data = ActivityDataArrayHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.current_activity)
        tvPageTitle.text = "New Activity"

        //initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this!!)
        fetchLocation()

        // Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        //set current as active in navigation
        bottomNavigationView.getMenu().findItem(R.id.activity_page).setChecked(true)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.activity_page -> {
                    // Go to CurrentActivity
                    val intent = Intent(this, CurrentActivity::class.java)
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    // Finish Activity
                    finish()
                    true
                }
                R.id.history_page -> {
                    // Go to HistoryActivity
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    // Finish Activity
                    finish()
                    true
                }
                R.id.stats_page -> {
                    // Go to StatActivity
                    val intent = Intent(this, StatActivity::class.java)
                    startActivity(intent)
                    // Finish Activity
                    finish()
                    true
                }
                R.id.profile_page -> {
                    // Go to ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    // Finish Activity
                    finish()
                    true
                }
                else -> false
            }
        }








        // get reference to  Start button
        val btnStart = findViewById(R.id.btnStart) as Button
        // get reference to  Stop button
        val btnStop = findViewById(R.id.btnStop) as Button
        // get reference to Pause button
        val btnResume = findViewById(R.id.btnResume) as Button
        // get reference to Pause button
        val btnEnd = findViewById(R.id.btnEnd) as Button

        // set on-click listener
        btnStart.setOnClickListener {
            startActivity()
        }

        // btnStop
        btnStop.setOnClickListener {
            stopActivity()
        }

        //btnResume
        btnResume.setOnClickListener {
            resumeActivity()
        }

        //btnEnd
        btnEnd.setOnClickListener {
            endActivity()
        }

    }




    // start Activity
    private fun startActivity(){

        //get time.now
        val StartTime= getDateTimeNow()

        //save location to preferences
        //preferences.setStartLocation(this, StartTime, currentLocation.latitude.toString(), currentLocation.longitude.toString() )

        //save datastring to array
        val saveString = getDateTimeNow() + " " + currentLocation.latitude.toString() + " " + currentLocation.longitude.toString()
        data.insterData(saveString)

        //Toast.makeText(applicationContext, currentLocation.latitude.toString() + " -- " +  currentLocation.longitude + " saved to prefs", Toast.LENGTH_SHORT).show()

        // button ausblenden
        btnStart.setVisibility(View.GONE)

        // show stop button
        btnStop.setVisibility(View.VISIBLE)

        // timer starten und anzeigen

        //save data to array

        //remove bottomnav?

        // alle 10 Sekunden die aktuelle position mit timestamp abspeichern

        // hide text
        tvPageTitle.text = ""
    }

    // Stop Avtivity
    private fun stopActivity(){
        //get time.now
        val EndTime = getDateTimeNow()
        //save location to preferences
        //preferences.setStartLocation(this, EndTime, currentLocation.latitude.toString(), currentLocation.longitude.toString() )

        //save datastring to array
        val saveString = getDateTimeNow() + " " + currentLocation.latitude.toString() + " " + currentLocation.longitude.toString()
        data.insterData(saveString)
        // timer stoppen

        // hide button Stop
        btnStop.setVisibility(View.GONE)

        //show button resume
        btnResume.setVisibility(View.VISIBLE)

        //show button End
        btnEnd.setVisibility(View.VISIBLE)

        // show message
        tvPageTitle.text = "Paused"
    }

    // Resume Activity
    private fun resumeActivity(){
        Toast.makeText(applicationContext, " Activity resumed", Toast.LENGTH_SHORT).show()
        //save datastring to array
        val saveString = getDateTimeNow() + " " + currentLocation.latitude.toString() + " " + currentLocation.longitude.toString()
        data.insterData(saveString)

        // timer stoppen

        //show stop button
        btnStop.setVisibility(View.VISIBLE)

        // hide resume button
        btnResume.setVisibility(View.GONE)

        // hide end button
        btnEnd.setVisibility(View.GONE)

        // hide message
        tvPageTitle.text = ""
    }

    // End Activity
    private fun endActivity(){
        Toast.makeText(applicationContext, " Activity end, show results on map", Toast.LENGTH_SHORT).show()

        // go to result acitvity and show results
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
        val task = fusedLocationClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                    currentLocation = location
                    // Toast.makeText(applicationContext, currentLocation.latitude.toString() + " -- " +  currentLocation.longitude, Toast.LENGTH_SHORT).show()
                    val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment?)!!
                    supportMapFragment.getMapAsync(this)
            }
            else {
                Toast.makeText(applicationContext, "no location found", Toast.LENGTH_SHORT).show()
            }

            }
        }

    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap?.addMarker(markerOptions)

        // show start button
        btnStart.setVisibility(View.VISIBLE)



      }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }

    fun getDateTimeNow():String {
        DateTimeFormatter.ISO_INSTANT.format(Instant.now())

        return DateTimeFormatter
            .ofPattern("yyyy-MM-dd_HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
    }



}
