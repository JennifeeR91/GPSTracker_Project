package com.gpstracker.gpstracker_project.activity



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.gpstracker.gpstracker_project.*
import kotlinx.android.synthetic.main.result_activity.*
import java.util.concurrent.TimeUnit

// todo: input Textfeld für note einfügen, oder auch dropdown für activity type
// todo: map mit track anzeigen
// todo: id sollte bei activity nicht notwendig sein, wird eh nicht für den Datenbank einterag verwendet


class ResultActivity : AppCompatActivity() , OnMapReadyCallback {

    private val preferences = Preferences()
    private val data = ActivityDataArrayHandler()
    private val db = Database(this)
    //private val dataArray = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
        // page Title
        tvPageTitle.text = "Activity Summary"
        val dataArray = data.get()

        /*
        // output data as text
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 16f
        // ausgabe zum testen
        //println("+++++++++ start")
        //for (i in dataArray) {
        //    println(i)
        //}
        //println("+++++++++ ende")

        for (i in dataArray) {
            tv_dynamic.append(i + System.getProperty("line.separator"))
        }
        layout.addView(tv_dynamic)
        */

        //show Time
        // get time from first
        val startTime = dataArray.first().split(" ")[0]
        // get time from last entry
        val endTime = dataArray.last { it.length > 3 }.split(" ")[0]
        // get difference and show result in h:m:s
        val diffTime = endTime.toLong() - startTime.toLong()
        val periodAsHH_MM_SS = java.lang.String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(diffTime),
                TimeUnit.MILLISECONDS.toMinutes(diffTime) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(diffTime) % TimeUnit.MINUTES.toSeconds(1))
        timer.text = "Duration: " + periodAsHH_MM_SS + System.getProperty ("line.separator") + "Distance: ---- ----"






        // get reference to  Start button
        val btnSave = findViewById(R.id.btnSave) as Button
        // get reference to  Stop button
        val btnCancel = findViewById(R.id.btnCancel) as Button
        // get reference to Pause button
        val btnResume = findViewById(R.id.btnResume) as Button

        // set on-click listener
        btnSave.setOnClickListener {
            saveActivity()
        }

        // set on-click listener
        btnCancel.setOnClickListener {
            cancelActivity()
        }

        // set on-click listener
        btnResume.setOnClickListener {
            resumeActivity()
        }

        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragment_map1) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)


    }

    // when button resume is pressed
    private fun resumeActivity() {
        Toast.makeText(applicationContext, " go back to current activity and resume", Toast.LENGTH_SHORT).show()

        // go back to currentActivity
        val intent = Intent(this, CurrentActivity::class.java)
        startActivity(intent)
        finish()
    }

    // when button cancel is pressed
    private fun cancelActivity() {
        // delete Data Array
        data.del()

        // go to history acitvity
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    // when button save is pressed
    private fun saveActivity() {
        Toast.makeText(applicationContext, " save to Database and go to history activity", Toast.LENGTH_SHORT).show()
        //val activity = 1


        // Get AvtivityData to save to DB
        val dataArray = data.get()
        var counter = 0
        var startstring: String = ""
        var endstring: String = ""


        for (i in dataArray) {
            // leere zeilen auslassen
            //split string and get separate values
            // save dataset to database
                if(counter == 0) {  startstring = i}
            endstring = i
            counter++
        }

        val startArr = startstring.split(" ").toTypedArray()
        val endArr = startstring.split(" ").toTypedArray()

        // Testausgaben
        //startArr.forEach {  Log.i("ArrayItem", " Array item=" + it ) }
        //endArr.forEach {  Log.i("ArrayItem", " Array item=" + it ) }
        //Log.i(startArr[0], "-" + startArr[0])

        // activity erstellen
        val activity = Activity(1, startArr[1].toDouble(), endArr[1].toDouble(), startArr[2].toDouble(), endArr[2].toDouble(), startArr[0].toLong(), endArr[0].toLong(), "note time", false)

        //Save to database
        db.insertActivity(activity)

        // delete Data Array
        data.del()

        // go to history acitvity
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        // alle daten ausgeben
        val dataArray = data.get()
        // get first point
        val firstlat = dataArray.first().split(" ")[1].toDouble()
        val firstlong = dataArray.first().split(" ")[2].toDouble()

        // get last point, last entry could be empty therefore the length >3
        val lastlat = dataArray.last { it.length > 3 }.split(" ")[1].toDouble()
        val lastlong = dataArray.last { it.length > 3 }.split(" ")[2].toDouble()

        val latLngStart = LatLng(firstlat, firstlong)
        val latLngEnd = LatLng(lastlat, lastlong)

        val startMarker = MarkerOptions().position(latLngStart).title("Startpoint")
        val endMarker = MarkerOptions().position(latLngEnd).title("Endpoint")

        //googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLngStart))
        //googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 15f))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 15F))

        // add marker
        googleMap?.addMarker(startMarker)
        googleMap?.addMarker(endMarker)

        // mir schleife durch alle punkte durchgehen und sie hinzufügen
        // Adding points to ArrayList
        val coordList = ArrayList<LatLng>()


        // additional testpoint
        coordList.add(LatLng(47.093, 15.436))

        //println("+++++++++ start")
        for (i in dataArray) {
            if (i.isNotEmpty()) {
                println(i)
                var x = i.split(" ")
                println(x[1])
                println(x[2])
                coordList.add(LatLng(x[1].toDouble(), x[2].toDouble()))
            }

        }
        //println("+++++++++ ende")

        //addintional test point
         coordList.add(LatLng(47.072, 15.396))

        val polyline1 = googleMap?.addPolyline(PolylineOptions().addAll(coordList))


    }



}

