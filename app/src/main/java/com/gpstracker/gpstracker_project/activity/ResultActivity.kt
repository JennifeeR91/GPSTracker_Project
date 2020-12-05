package com.gpstracker.gpstracker_project.activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gpstracker.gpstracker_project.*
import kotlinx.android.synthetic.main.result_activity.*

// todo: input Textfeld für note einfügen, oder auch dropdown für activity type
// todo: timestamp beim zum abspeichern: getTime und eine funktion zum formatieren, falls die zeit ausgegeben wird
// todo: id sollte bei activity nicht notwendig sein


class ResultActivity : AppCompatActivity() {

    private val preferences = Preferences()
    private val data = ActivityDataArrayHandler()
    private val db = Database(this)
    //private val dataArray = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
        // page Title
        tvPageTitle.text = "Activity Summary"

        // output data as text
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 16f
        val dataArray = data.get()
        // ausgabe zum testen
        //println("+++++++++ start")
        //for (i in dataArray) {
        //    println(i)
        //}
        //println("+++++++++ ende")

        for (i in dataArray) {
            tv_dynamic.append(i+  System.getProperty ("line.separator"))
        }
        layout.addView(tv_dynamic)


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

        // btnStop
        btnCancel.setOnClickListener {
            cancelActivity()
        }

        //btnResume
        btnResume.setOnClickListener {
            resumeActivity()
        }


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
        // wenn timestamp richtig formatiert dann das:
        //val activity = Activity(1, startArr[1].toDouble(), endArr[1].toDouble(), startArr[2].toDouble(), endArr[2].toDouble(), startArr[0].toLong(), endArr[0].toLong(), "note", false)
        // zum testen des eintrags das:
        val activity = Activity(1, startArr[1].toDouble(), endArr[1].toDouble(), startArr[2].toDouble(), endArr[2].toDouble(), 555555.toLong(), 5555554.toLong(), "note", false)



        //Save to database
        db.insertActivity(activity)

        // delete Data Array
        data.del()

        // go to history acitvity
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }

}

