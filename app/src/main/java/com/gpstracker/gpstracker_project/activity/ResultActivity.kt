package com.gpstracker.gpstracker_project.activity



import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import java.lang.Math.round
import java.util.concurrent.TimeUnit

//  dropdown für activity type
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
        val startTime = dataArray.first().split(" ")[0].toLong()
        // get time from last entry
        val endTime = dataArray.last { it.length > 3 }.split(" ")[0].toLong()
        val Time = getDuration(startTime, endTime)
        timer.text = "Duration: " + Time + System.getProperty("line.separator")


        // get Distance
        val distance = getTotalDistance()
        timer.append("Distance: " + distance )


        /*
        // get reference to Save button
        val btnSave = findViewById(R.id.btnSave) as Button
        // get reference to Cancel button
        val btnCancel = findViewById(R.id.btnCancel) as Button
        // get reference to Resume button
        val btnResume = findViewById(R.id.btnResume) as Button

         */

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
        dataArray.forEach {  Log.i("ArrayItem", " Array item=" + it) }

        var counter = 0
        var startstring: String = ""
        var endstring: String = ""


        for (i in dataArray) {
            // leere zeilen auslassen
            if(i.isNotEmpty()){
                if(counter == 0) {  startstring = i}
                endstring = i
                counter++
            }
        }
        //println("***************************************")
        //println("startstring: "+startstring)
        //println("endstring: " + endstring)
        //println("***************************************")


        //split string and get separate values
        val startArr = startstring.split(" ").toTypedArray()
        val endArr = endstring.split(" ").toTypedArray()

        // Testausgaben
        //startArr.forEach {  Log.i("ArrayItem", " Array item=" + it ) }
        //endArr.forEach {  Log.i("ArrayItem", " Array item=" + it ) }
        //Log.i(startArr[0], "-" + startArr[0])

        // get note from input field
        val note = activityType.text.toString()

        // activity erstellen
        val activity = Activity(1, startArr[1].toDouble(), endArr[1].toDouble(), startArr[2].toDouble(), endArr[2].toDouble(), startArr[0].toLong(), endArr[0].toLong(), note, false)

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

    // add up all distances between all points
    public fun getTotalDistance(): Double {
        // get stored data
        val dataArray = data.get()

        var distance:Double = 0.0
        var long1:Double = 0.0
        var lat1:Double = 0.0

        // add test points
        dataArray.add(System.currentTimeMillis().toString() + " " + "47.0800497 15.4105737")

        // loop through all points
        for(i in dataArray){
            //println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - -")
            //println(i)
            if(i.isNotEmpty()) {
                //println(" - - - - IN IF 1- - - - - - - - - - - - - - - - - - - - - - - - -")

                if (long1 == 0.0) {
                    println("long: ist 0")
                }else{
                    println(" - - - - IN IF 2  - - - - - - - - - - - - - - - - - - - - - - - -")
                    distance = distance + this.getDistanceFromLatLonInKm(long1, lat1, i.split(" ")[1].toDouble(), i.split(" ")[2].toDouble())
                    println(distance)
                    println("long: "+ long1)
                    println("lat: " + lat1)
                }
                long1 = i.split(" ")[1].toDouble()
                lat1 = i.split(" ")[2].toDouble()

            }
        }


        return distance.toDouble().round(2)
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }


    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI/180)
    }


    private fun getDistanceFromLatLonInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        //dist = dist * 60 * 1.1515
        dist = dist * 60 * 1.1515 * 1.609344
        println("Distance p2p: " + dist)

        return dist
    }



    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

}

