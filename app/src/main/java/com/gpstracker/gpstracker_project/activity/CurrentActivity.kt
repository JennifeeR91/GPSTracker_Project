package com.gpstracker.gpstracker_project.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.current_activity.*


class CurrentActivity : AppCompatActivity(), OnMapReadyCallback {

   // private var googleMap: GoogleMap? = null
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.current_activity)
        tvPageTitle.text = "Activity"

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
                    Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" +
                            currentLocation.longitude, Toast.LENGTH_SHORT).show()
                    val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map) as
                            SupportMapFragment?)!!
                    supportMapFragment.getMapAsync(this)
                }
            }
        }

    override fun onMapReady(googleMap: GoogleMap?) {
      // this.googleMap = googleMap
       val latitudeStart = 10.3181466
       val longitudeStart =  123.9029382
     //  val latitudeEnd = 10.311795
      // val longitudeEnd =  123.915864

         googleMap!!.addMarker(MarkerOptions().position(LatLng(latitudeStart, longitudeStart)).title("Ayala"))
    //    googleMap!!.addMarker(MarkerOptions().position(LatLng(latitudeEnd, longitudeEnd)).title("SM City"))

     //  googleMap!!.addMarker(MarkerOptions().position(LatLng(currentLocation.latitude, currentLocation.longitude)).title("Ayala"))


      }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        }
        }
    }
}
