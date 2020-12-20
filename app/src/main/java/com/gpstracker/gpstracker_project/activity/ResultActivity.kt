package com.gpstracker.gpstracker_project.activity



import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import kotlin.math.*

//  dropdown für activity type



class ResultActivity : AppCompatActivity() , OnMapReadyCallback {

    private val preferences = Preferences()
    private val data = ActivityDataArrayHandler()
    private val db = Database(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
        // page Title
        tvPageTitle.setText(R.string.activitySummary)
        val dataArray = data.get()

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

    // show duration in hh:mm:ss from timestamp difference
    @SuppressLint("DefaultLocale")
    fun getDuration(start: Long, end: Long): String {
        // get difference and show result in h:m:s
        val diffTime = end - start.toLong()
        return java.lang.String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(diffTime),
                TimeUnit.MILLISECONDS.toMinutes(diffTime) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(diffTime) % TimeUnit.MINUTES.toSeconds(1))

    }

    // when button resume is pressed
    private fun resumeActivity() {
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

    // when button save is pressed, save to Database and go to history activity
    private fun saveActivity() {
        // Get ActivityData to save to DB
        val dataArray = data.get()
        //dataArray.forEach {  Log.i("ArrayItem", " Array item=" + it) }

        var counter = 0
        var startstring = ""
        var endstring = ""


        for (i in dataArray) {
            // leere zeilen auslassen
            if(i.isNotEmpty()){
                if(counter == 0) {  startstring = i}
                endstring = i
                counter++
            }
        }
        //split string and get separate values
        val startArr = startstring.split(" ").toTypedArray()
        val endArr = endstring.split(" ").toTypedArray()

        // get note from input field
        val note = activityType.text.toString()

        //get total distance for write into db
        val totalDistance = getTotalDistance()

        // Setup activity
        val activity = Activity(1, startArr[1].toDouble(), endArr[1].toDouble(), startArr[2].toDouble(), endArr[2].toDouble(), startArr[0].toLong(), endArr[0].toLong(), note, totalDistance, 1, false)
        //Save to database
        db.insertActivity(activity)


        // SAVE waypoints
        // get id from last activity saved
        val activityId = db.getLastActivityID()

        //write every point in DB
        for (i in dataArray) {
            // leere zeilen auslassen
            if(i.isNotEmpty()){
                val pointSet = i.split(" ")
                println("waypoints: ID="+activityId  + " timestamp: " + pointSet[0] + " lat: " + pointSet[1] + " long: " + pointSet[2])
                db.insertPosition(activityId,pointSet[0].toLong(), pointSet[1].toDouble(), pointSet[2].toDouble() )
            }
        }

        // delete Data Array
        data.del()

        // go to history activity
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

        val startMarker = MarkerOptions().position(latLngStart).title("Startpoint: "+ firstlat + ", "+ firstlong)
        val endMarker = MarkerOptions().position(latLngEnd).title("Endpoint: " + lastlat + ", " + lastlong)

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 15F))

        // add marker
        googleMap?.addMarker(startMarker)
        googleMap?.addMarker(endMarker)

        // mit schleife durch alle punkte durchgehen und sie hinzufügen
        // Adding points to ArrayList
        val coordList = ArrayList<LatLng>()
        // additional testpoint
        //coordList.add(LatLng(47.093, 15.436))

        println("+++++++++ start")
        for (i in dataArray) {
            if (i.isNotEmpty()) {
                println(i)
                val x = i.split(" ")
                println(x[1])
                println(x[2])
                coordList.add(LatLng(x[1].toDouble(), x[2].toDouble()))
                //addintional test point
                //coordList.add(LatLng(47.072, 15.396))
            }

        }
        println("+++++++++ ende")



        val polyline1 = googleMap?.addPolyline(PolylineOptions().addAll(coordList))

    }

    // add up all distances between all points
    private fun getTotalDistance(): Double {
        // get stored data
        val dataArray = data.get()
        // returns 0:timestamp + " " + 1:lat + " " + 2:long

        var distance = 0.0
        var long1 = 0.0
        var lat1 = 0.0

        // add test points
        //dataArray.add(System.currentTimeMillis().toString() + " " + "47.0800497 15.4105737")
        //dataArray.add(System.currentTimeMillis().toString() + " " + "47.0800437 15.4305728")

        // startpoint again
        val firstlat = dataArray.first().split(" ")[1].toDouble()
        val firstlong = dataArray.first().split(" ")[2].toDouble()

        val testval = 0.008
        val testlat = firstlat + testval
        val testlong = firstlong + testval
        val testlat1 = firstlat - testval
        val testlong1 = firstlong - testval
        val testlat2 = firstlat - testval
        val testlong2 = firstlong + testval
        val testlat3 = firstlat + testval
        val testlong3 = firstlong - testval

        // addd testpooints
        dataArray.add(System.currentTimeMillis().toString() + " " + testlat.toString() + " " + testlong.toString())
        dataArray.add(System.currentTimeMillis().toString() + " " + testlat1.toString() + " " + testlong1.toString())
        dataArray.add(System.currentTimeMillis().toString() + " " + testlat2.toString() + " " + testlong2.toString())
        dataArray.add(System.currentTimeMillis().toString() + " " + testlat2.toString() + " " + testlong2.toString())
        dataArray.add(System.currentTimeMillis().toString() + " " + testlat3.toString() + " " + testlong3.toString())
        // add fistpoint as last point
        dataArray.add(System.currentTimeMillis().toString() + " " + firstlat.toString() + " " + firstlong.toString())


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
                    distance += this.getDistanceFromLatLonInKm(lat1,long1, i.split(" ")[1].toDouble(), i.split(" ")[2].toDouble())
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


    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI/180)
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



    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / PI
    }

}

