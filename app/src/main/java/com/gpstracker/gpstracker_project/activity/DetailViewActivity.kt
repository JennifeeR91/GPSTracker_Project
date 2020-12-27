package com.gpstracker.gpstracker_project.activity

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.gpstracker.gpstracker_project.Database
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.detail_view_activity.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*


// Todo: show edit button

class DetailViewActivity : AppCompatActivity() , OnMapReadyCallback {

    private val db = Database(this)
    // new instance of resultactivity class to use getTime function
    private val result = ResultActivity()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_view_activity)

        // get ID for Detailview
        val id = intent.getLongExtra("id", -1)
        // get data from database
        db.getActivity(id)


        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map_detail) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)


        if (id >= 0) {
            showDataAsText(id)
        }else{
            tvPageTitle.setText(R.string.error_no_id)
        }

        // set onclick listener
        btnDelete.setOnClickListener {
            deleteActivity(id)
        }

        // Bottom Navigation
        showBottomNavigation()
    }

    private fun showBottomNavigation() {
        //set current as active in navigation
        bottom_navigation.menu.findItem(R.id.history_page).isChecked = true

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.activity_page -> {
                    // Go to CurrentActivity
                    val intent = Intent(this, CurrentActivity::class.java)

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

    private fun deleteActivity(id: Long) {
        // delete from DB
        val activity = db.getActivity(id)
        if (activity != null) {
            db.delActivity(activity)
            Toast.makeText(applicationContext, "Deleted from DB", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext, "$id NOT deleted from DB", Toast.LENGTH_SHORT).show()
        }

        // return to history activity
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showDataAsText(id: Long) {
        // get data from database
        val activity = db.getActivity(id)

        // get time
        val time = result.getDuration(activity!!.starttime, activity.endtime)

        // get Date from starttime
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm") // "dd/MM/yy hh:mm"
        val netDate = Date(activity.starttime)
        val date =sdf.format(netDate)



        //get items of array
        val gaits = resources.getStringArray(R.array.horse_gaits)
        val gait = activity.activitytype.toInt()


        // get City from location
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(activity.startlat, activity.startlong, 1)
        val address: String = addresses[0].getAddressLine(0)
        val postcode = address.split(", ")

        //val stateName: String = addresses[0].getAddressLine(1)
        //val countryName: String = addresses[0].getAddressLine(2)

        tvPageTitle.text = gaits[gait] + ", " + postcode[1]

        val distance = getTotalDistance(id)

        summary.append(
                date + System.getProperty("line.separator") +
                        "Duration: " + time

        )
        summary_right.append(
                "Distance:  $distance km")

        if (activity.note.isNotEmpty()) {
            summary_right.append(System.getProperty("line.separator") + "Note: " + activity.note)
        }
        return
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val id = intent.getLongExtra("id", -1)
        // get data from database
        val activity = db.getActivity(id)


        val startlat = activity?.startlat
        val startlong = activity?.startlong
        val endlat = activity?.endlat
        val endlong = activity?.endlong

        val latLngStart = LatLng(startlat!!, startlong!!)
        val latLngEnd = LatLng(endlat!!, endlong!!)

        val startMarker = MarkerOptions().position(latLngStart).title("startpoint: $startlong $startlat")
        val endMarker = MarkerOptions().position(latLngEnd).title("endpoint: $endlong $endlat")

        // set zoom to startpoint
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 15F))

        // add marker
        googleMap?.addMarker(startMarker)
        googleMap?.addMarker(endMarker)


        // get points from database to draw the path:
        val points = db.getPoints(id)
        // retrungs 0:timestamp + " " + 1:lat + " " + 2:long

        println("GET THE WAYPOINTS: $points")

        val coordList = ArrayList<LatLng>()
        // Adding points to ArrayList

        for (i in points) {
            if (i.isNotEmpty()) {
                val x = i.split(" ")
                coordList.add(LatLng(x[1].toDouble(), x[2].toDouble()))
            }
        }
        println(coordList)
        val polyline = googleMap?.addPolyline(PolylineOptions().addAll(coordList))

    }

    private fun getTotalDistance(id: Long): Double {
        // get stored data
        val points = db.getPoints(id)
        // DB: returns 0:timestamp + " " + 1:lat + " " + 2:long

        var distance = 0.0
        var long1 = 0.0
        var lat1 = 0.0

        // loop through all points
        for(i in points){
            if(i.isNotEmpty()) {
                if (long1 == 0.0) {
                    println("long: ist 0")
                }else{
                    println(" - - - - IN IF 2  - - - - - - - - - - - - - - - - - - - - - - - -")
                    println("should be lat: " + i.split(" ")[1].toDouble())
                    println("should be long: " + i.split(" ")[2].toDouble())
                    distance += this.getDistanceFromLatLonInKm(lat1, long1, i.split(" ")[1].toDouble(), i.split(" ")[2].toDouble())
                    println(distance)
                    println("long: $long1")
                    println("lat:  $lat1")
                }
                long1 = i.split(" ")[2].toDouble()
                lat1 = i.split(" ")[1].toDouble()

            }
        }


        return distance.round(2)
    }


    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (this * multiplier).roundToInt() / multiplier
    }

    private fun getDistanceFromLatLonInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        //dist = dist * 60 * 1.1515
        dist *= 60 * 1.1515 * 1.609344
        println("Distance p2p: $dist")

        return dist
    }


    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI/180)
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / PI
    }

}