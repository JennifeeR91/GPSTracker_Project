package com.gpstracker.gpstracker_project.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color.RED
import android.location.Location
import android.os.*
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gpstracker.gpstracker_project.ActivityDataArrayHandler
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.current_activity.*

// todo: resume timer after resultActivity with correct time

class CurrentActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionCode = 101
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val coordList = ArrayList<LatLng>()
    private val data = ActivityDataArrayHandler()

    // run task every 10 seconds
    lateinit var mainHandler: Handler
    private val updateTextTask = object : Runnable {
        override fun run() {
            // get position
            fetchLocation()
            // write to DataArray
            writeCurrentDataToArray()
            // delay 1 sec.
            mainHandler.postDelayed(this, 1000)
        }
    }

    // timer variable
    private var mLastStopTime:Long = 0

    /*
    var mIntent = intent
    var resumeTime = mIntent.getIntExtra("resumeTime", 0L)
     */

    

    // fist method called
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.current_activity)
        // set title
        tvPageTitle.setText(R.string.newActivity)
        //initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        getLocationUpdates()



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
        // set button start visable
        btnStart.visibility = View.VISIBLE
    }

    // show bottom navigation
    private fun showBottomNavigation() {
        //set current as active in navigation
        bottom_navigation.menu.findItem(R.id.activity_page).isChecked = true

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.activity_page -> {
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

    // when resuming activity
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        //get last known location
        fetchLocation()

    }

    // start Activity
    private fun startActivity(){
        // start looper
        mainHandler.post(updateTextTask)
        // bottom menu ausblenden
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE
        // hide start button
        btnStart.visibility = View.GONE
        // hide resume button
        btnResume.visibility = (View.GONE)
        // show stop button
        btnStop.visibility = (View.VISIBLE)
        // Timer starten
        chronoStart()
        // hide title
        tvPageTitle.visibility = (View.GONE)
        // hide "paused"
        tvPaused.visibility = (View.GONE)
    }

    // Stop Avtivity
    private fun stopActivity(){
        // stop looper
        mainHandler.removeCallbacks(updateTextTask)
        // timer stoppen
        chronoPause()
        // hide button Stop
        btnStop.visibility = View.GONE
        //show button resume
        btnResume.visibility = View.VISIBLE
        //show button End
        btnEnd.visibility = (View.VISIBLE)
        // show title
        tvPaused.visibility = (View.VISIBLE)
        tvPaused.setText(R.string.paused)
    }

    // Resume Activity
    private fun resumeActivity(){
        // start looper again
        mainHandler.post(updateTextTask)
        // show Toast info
        Toast.makeText(applicationContext, " Activity resumed", Toast.LENGTH_SHORT).show()
        // resume timer
        chronoStart()
        //show stop button
        btnStop.visibility = (View.VISIBLE)
        // hide resume button
        btnResume.visibility = (View.GONE)
        // hide end button
        btnEnd.visibility = (View.GONE)
        // hide "paused"
        tvPaused.visibility = (View.GONE)
    }

    // End Activity, go to result activity and show results
    private fun endActivity(){
        //stop looper
        mainHandler.removeCallbacks(updateTextTask)
        // go to result activity and show results
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("resumeTime", mLastStopTime)
        startActivity(intent)
        finish()
    }

    private fun chronoStart() {
        // on first start
        if (mLastStopTime == 0L) {
            simpleChronometer.base = SystemClock.elapsedRealtime()
        }
        else {
            val intervalOnPause: Long = SystemClock.elapsedRealtime() - mLastStopTime
            simpleChronometer.base = (simpleChronometer.base + intervalOnPause)
        }
        simpleChronometer.visibility = (View.VISIBLE)
        simpleChronometer.start()
    }

    private fun chronoPause() {
        simpleChronometer.stop()
        mLastStopTime = SystemClock.elapsedRealtime()
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
                tvPageTitle.setText(R.string.no_location)
            }
        }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        MarkerOptions().position(latLng).title("My position")

        //drow path:
        coordList.add(LatLng(currentLocation.latitude, currentLocation.longitude))

        val polyline2 = googleMap?.addPolyline(PolylineOptions().addAll(coordList))
        polyline2?.color = RED
        polyline2?.endCap = RoundCap()

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17F))

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
        data.insertData(saveString)

    }

    private fun getLocationUpdates()    {
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    locationResult.lastLocation
                }
            }
        }
    }

    //start location updates
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

}
