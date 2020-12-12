package com.gpstracker.gpstracker_project.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gpstracker.gpstracker_project.Database
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.detail_view_activity.*


// todo: show duration and distance
// todo: show delete button



class DetailViewActivity : AppCompatActivity() , OnMapReadyCallback {

    private val db = Database(this)
    // get data from database

    // new instance of resultactivity class to use getTime function
    val result = ResultActivity()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_view_activity)

        // get ID for Detailview
        val id = intent.getLongExtra("id", -1)


        // page Title
        tvPageTitle.text = "Activity Detail"
        tvPageTitle.append(" " + id)
        //val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map1) as SupportMapFragment?)!!
        //supportMapFragment.getMapAsync(this)

        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map_detail) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)


        if (id >= 0) {

            showDataAsText(id)

        }else{
            Toast.makeText(applicationContext,  "Error: No ID given!!" , Toast.LENGTH_SHORT).show()
        }


        // Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        //set current as active in navigation
        bottomNavigationView.getMenu().findItem(R.id.history_page).setChecked(true)
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



    private fun showDataAsText(id:Long) {
        // get data from database
        var activity = db.getActivity(id)

        // output data as text
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 20f
        tv_dynamic.setBackgroundColor(333342)


        // ausgabe der Datenbankeinträge, sollte dann in eine eigene funktion

            // get time
        //val time = result.getDuration(i.starttime, i.endtime )



            tv_dynamic.append(
                   // "id: " + activity?.id  +System.getProperty ("line.separator")+
                    //"startlong: " + activity?.startlong.toString()  +System.getProperty ("line.separator")+
                    //"endlong: " + activity?.endlong  +System.getProperty ("line.separator")+
                    //"startlat: " + activity?.startlat  +System.getProperty ("line.separator")+
                    //"endlat: " + activity?.endlat  +System.getProperty ("line.separator")+
                    "starttime: " + activity?.starttime  +System.getProperty ("line.separator")+
                    "endtime: " + activity?.endtime  +System.getProperty ("line.separator")+
                    "note: " + activity?.note  +System.getProperty ("line.separator")

            )




        layout.addView(tv_dynamic)
        return

    }




    override fun onMapReady(googleMap: GoogleMap?) {
        val id = intent.getLongExtra("id", -1)
        // get data from database
        var activity = db.getActivity(id)


        var startlat = activity?.startlat
        var startlong = activity?.startlong
        var endlat = activity?.endlat
        var endlong = activity?.endlong

        val latLngStart = LatLng(startlong!!, startlat!!)
        val latLngEnd = LatLng(endlong!!, endlat!!)

        val startMarker = MarkerOptions().position(latLngStart).title(startlong.toString() + " " + startlat.toString() )
        val endMarker = MarkerOptions().position(latLngEnd).title(endlong.toString() + " " + endlat.toString() )


        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 15F))

        // add marker
        googleMap?.addMarker(startMarker)
        googleMap?.addMarker(endMarker)
    }


    }