package com.gpstracker.gpstracker_project.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gpstracker.gpstracker_project.ActivityDataArrayHandler
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.current_activity.*

// todo: show timer on Start
// Todo: map follows gps
// Todo: add Database Field "type"
// Save waypoints to new table
// all Texts in XML file to language file
// all Colors to Color File



class CurrentActivity : AppCompatActivity(), OnMapReadyCallback {

   // private var googleMap: GoogleMap? = null
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionCode = 101
    private lateinit var locationCallback: LocationCallback

    // run task every 10 seconds
    lateinit var mainHandler: Handler
    private val updateTextTask = object : Runnable {
        override fun run() {
            // TIMER
            //plusOneSecond()

            // write to DataArray
            writeCurrentDataToArray()


            // get position
            fetchLocation()

            mainHandler.postDelayed(this, 1000)
        }
    }
    private var secondsLeft = 0



    private val data = ActivityDataArrayHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.current_activity)
        tvPageTitle.setText(R.string.newActivity)

        //initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        // Bottom Navigation
        showBottomNavigation()

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

        // handler for looping
        mainHandler = Handler(Looper.getMainLooper())
    }

    private fun showBottomNavigation() {
        //set current as active in navigation
        bottom_navigation.getMenu().findItem(R.id.activity_page).setChecked(true)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.activity_page -> {
                    /*
                    // Go to CurrentActivity
                    val intent = Intent(this, CurrentActivity::class.java)
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    // Finish Activity
                    finish()
                    */
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
    }

    fun plusOneSecond() {
        //if secondsLeft > 0 {
            secondsLeft += 1
            println("here--. "+secondsLeft)
        //}
    }

    override fun onResume() {
        super.onResume()

        //get last known location
        fetchLocation()

    }

    // start Activity
    private fun startActivity(){
        mainHandler.post(updateTextTask)

        // button ausblenden
        btnStart.setVisibility(View.GONE)

        // bottom menu ausblenden
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setVisibility(View.GONE)

        // show stop button
        btnStop.setVisibility(View.VISIBLE)

        // hide start button
        btnStart.setVisibility(View.GONE)

        // hide resume button
        btnResume.setVisibility(View.GONE)

        // timer starten und anzeigen

        //save data to array

        // alle 10 Sekunden die aktuelle position mit timestamp abspeichern

        // hide title
        tvPageTitle.setVisibility(View.GONE)
    }

    // Stop Avtivity
    private fun stopActivity(){
        // stop looper:
        mainHandler.removeCallbacks(updateTextTask)

        // timer stoppen

        // hide button Stop
        btnStop.setVisibility(View.GONE)

        //show button resume
        btnResume.setVisibility(View.VISIBLE)

        //show button End
        btnEnd.setVisibility(View.VISIBLE)

        // show title
        tvPageTitle.setVisibility(View.VISIBLE)
        tvPageTitle.setText(R.string.paused)
    }

    // Resume Activity
    private fun resumeActivity(){
        // start looper again
        mainHandler.post(updateTextTask)

        Toast.makeText(applicationContext, " Activity resumed", Toast.LENGTH_SHORT).show()


        // timer stoppen

        //show stop button
        btnStop.setVisibility(View.VISIBLE)

        // hide resume button
        btnResume.setVisibility(View.GONE)

        // hide end button
        btnEnd.setVisibility(View.GONE)

        // hide message
        tvPageTitle.setVisibility(View.GONE)
    }

    // End Activity, go to result activity and show results
    private fun endActivity(){
        mainHandler.removeCallbacks(updateTextTask)
        // go to result activity and show results
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchLocation_test() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment?)!!
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    currentLocation = location

                }
            }
        }
        supportMapFragment.getMapAsync(this)
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

        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment?)!!
        val task = fusedLocationClient.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                    currentLocation = location
                    //val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment?)!!
                    supportMapFragment.getMapAsync(this)
            }
            else {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult ?: return
                        for (location in locationResult.locations){
                            // Update UI with location data
                            currentLocation = location
                        }
                    }
                }
                //Toast.makeText(applicationContext, "No location found - please check your GPS connection", Toast.LENGTH_SHORT).show()
                //tvPageTitle.text = "No location found - please check your GPS connection"
            }

            }
        }

    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("My position")

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
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

    private fun writeCurrentDataToArray(){
        val saveString = System.currentTimeMillis().toString() + " " + currentLocation.latitude.toString() + " " + currentLocation.longitude.toString()
        data.insterData(saveString)
        Toast.makeText(applicationContext, " savestring: " + saveString, Toast.LENGTH_LONG).show()
    }


}
