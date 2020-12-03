package com.gpstracker.gpstracker_project.activity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gpstracker.gpstracker_project.Preferences
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.result_activity.*


class ResultActivity : AppCompatActivity() {

    private val preferences = Preferences()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
        // page Title
        tvPageTitle.text = "Activity Summary"


        // output data as text
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 16f
        tv_dynamic.text = preferences.getLocations(this)
        layout.addView(tv_dynamic)




    }



    // show buttons Save, Resume, Chancel

}

