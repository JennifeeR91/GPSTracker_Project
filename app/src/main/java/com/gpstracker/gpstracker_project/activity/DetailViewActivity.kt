package com.gpstracker.gpstracker_project.activity

import android.content.Intent
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

// Todo: (show edit button)
// todo: show  distance

class DetailViewActivity : AppCompatActivity() , OnMapReadyCallback {

    private val db = Database(this)
    // new instance of resultactivity class to use getTime function
    val result = ResultActivity()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_view_activity)

        // get ID for Detailview
        val id = intent.getLongExtra("id", -1)
        // get data from database
        var activity = db.getActivity(id)


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
        bottom_navigation.getMenu().findItem(R.id.history_page).setChecked(true)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
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

    private fun deleteActivity(id:Long) {
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

    private fun showDataAsText(id:Long) {
        // get data from database
        val activity = db.getActivity(id)

        // get time
        val time = result.getDuration(activity!!.starttime, activity.endtime)

        // page Title
        // get Date from starttime
        val sdf = SimpleDateFormat("dd/MM/yy hh:mm") // "dd/MM/yy hh:mm"
        val netDate = Date(activity.starttime)
        val date =sdf.format(netDate)
        tvPageTitle.text = activity.note

        summary.append(
                    date  +System.getProperty ("line.separator")+
                    "Duration: " + time  +System.getProperty ("line.separator")+
                    "Distance: " + "calculated distance"
            )
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

        val latLngStart = LatLng(startlong!!, startlat!!)
        val latLngEnd = LatLng(endlong!!, endlat!!)

        val startMarker = MarkerOptions().position(latLngStart).title("startpoint: $startlong $startlat")
        val endMarker = MarkerOptions().position(latLngEnd).title("endpoint: $endlong $endlat")

        // set zoom to startpoint
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 15F))

        // add marker
        googleMap?.addMarker(startMarker)
        googleMap?.addMarker(endMarker)


        // get points from database to draw the path:
        val points = db.getPoints(id)
        println("GET THE WAYPOINTS: $points")

        val coordList = ArrayList<LatLng>()
        // Adding points to ArrayList


        for (i in points) {

            if (i.isNotEmpty()) {
               // println("////////////////////////////////////////////////// " + i)
                val x = i.split(" ")
                println(x[1])
                println(x[2])
                coordList.add(LatLng(x[1].toDouble(), x[2].toDouble()))
                val marker = MarkerOptions()
                    .position(
                        LatLng(x[2].toDouble(),
                        x[1].toDouble()))
                    .title("zwischenpunkt: Time: "+ x[0] + " place: " + x[2] + " " + x[1])

                googleMap?.addMarker(marker)
            }

        }
        //addintional test point
        //coordList.add(LatLng(47.072, 15.396))

        println(coordList)
        val polyline1 = googleMap?.addPolyline(PolylineOptions().addAll(coordList))
    }

}