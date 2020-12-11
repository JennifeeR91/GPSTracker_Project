package com.gpstracker.gpstracker_project.activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gpstracker.gpstracker_project.Database
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.detail_view_activity.*




class DetailViewActivity : AppCompatActivity()  {

    private val db = Database(this)
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




        if (id >= 0) {
            showDataAsText(id)
        }else{
            Toast.makeText(applicationContext,  "Error: No ID given!!" , Toast.LENGTH_SHORT).show()
        }


    }

    private fun showDataAsText(id:Long) {
        // get data from database
        var activity = db.getActivity(id)

        // output data as text
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 20f


        // ausgabe der Datenbankeinträge, sollte dann in eine eigene funktion

            // get time
        //val time = result.getDuration(i.starttime, i.endtime )



            tv_dynamic.append(
                    "id: " + activity?.id  +System.getProperty ("line.separator")+
                    "startlong: " + activity?.startlong.toString()  +System.getProperty ("line.separator")+
                    "endlong: " + activity?.endlong  +System.getProperty ("line.separator")+
                    "startlat: " + activity?.startlat  +System.getProperty ("line.separator")+
                    "endlat: " + activity?.endlat  +System.getProperty ("line.separator")+
                    "starttime: " + activity?.starttime  +System.getProperty ("line.separator")+
                    "endtime: " + activity?.endtime  +System.getProperty ("line.separator")+
                    "note: " + activity?.note  +System.getProperty ("line.separator")

            )




        layout.addView(tv_dynamic)
        return

    }

    /*
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
    */

    }