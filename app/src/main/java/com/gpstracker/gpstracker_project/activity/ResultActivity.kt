package com.gpstracker.gpstracker_project.activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gpstracker.gpstracker_project.AcitvityData
import com.gpstracker.gpstracker_project.Preferences
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.result_activity.*


class ResultActivity : AppCompatActivity() {

    private val preferences = Preferences()
    private val data = AcitvityData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
        // page Title
        tvPageTitle.text = "Activity Summary"


        // output data as text
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 16f
        //tv_dynamic.text = preferences.getLocations(this)

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

    private fun resumeActivity() {
        Toast.makeText(applicationContext, " go back to current activity and resume", Toast.LENGTH_SHORT).show()

        // go to result acitvity and show results
        val intent = Intent(this, CurrentActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun cancelActivity() {
        Toast.makeText(applicationContext, " delete data array and go to history", Toast.LENGTH_SHORT).show()

        // go to result acitvity and show results
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveActivity() {
        Toast.makeText(applicationContext, " save to Database and go to history activity", Toast.LENGTH_SHORT).show()

        //Save data to database


        // go to result acitvity and show results
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }


    // show buttons Save, Resume, Chancel

}

