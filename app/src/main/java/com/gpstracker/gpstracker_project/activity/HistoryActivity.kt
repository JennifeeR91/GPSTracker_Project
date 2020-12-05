package com.gpstracker.gpstracker_project.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gpstracker.gpstracker_project.Database
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.result_activity.*

//todo: einträge korrekt auslesen und anzgeigen
//todo: Detailansicht erstellen oder Result Activity verwenden
//todo: einträge klickbar machen und zur detailansicht wechseln


class HistoryActivity : AppCompatActivity() {

    // new Database object instance
    val db = Database(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity)
        tvPageTitle.text = "History"


        // output data as text
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 16f

        val dataArray = db.getAllActivities()
        // ausgabe zum testen
        println("+++++++++ start")
        for (i in dataArray) {
            println(i)
        }
        println("+++++++++ ende")


        // ausgabe der Datenbankeinträge, sollte dann in eine eigene funktion
        var counter = 0
        for (i in dataArray) {
            counter ++
            tv_dynamic.append(
                    // i
                    counter.toString()
                    +
                    ".  eintrag"
                    +
                    System.getProperty ("line.separator")
            )
        }


        layout.addView(tv_dynamic)


        // Bottom Naviagation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        //set current as active in navigation
        bottomNavigationView.getMenu().findItem(R.id.history_page).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
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




}