package com.gpstracker.gpstracker_project.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gpstracker.gpstracker_project.*
import kotlinx.android.synthetic.main.history_activity.*
import kotlinx.android.synthetic.main.result_activity.*
import kotlinx.android.synthetic.main.result_activity.tvPageTitle

// Einträge über adapter und layout inflator anzeigen
//todo: Detailansicht erstellen oder Result Activity verwenden
//todo: einträge klickbar machen und zur detailansicht wechseln
// todo: Detailansicht löschen ermöglichen


class HistoryActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener {

    // new instance of Database
    val db = Database(this)
    // new instance of resultactivity class to use getTime function
    val result = ResultActivity()

    //initialize activityAdapter
    private var activityAdapter: ActivityAdapter?? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity)
        tvPageTitle.text = "History"




        val activities: List<Activity> = db.getAllActivities()
        activityAdapter = ActivityAdapter(this, activities)
        lvActivities.adapter = activityAdapter
        lvActivities.onItemClickListener = this




        // output data as text
        /*
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 16f

        // get data from database
        val dataArray = db.getAllActivities()

        // ausgabe der Datenbankeinträge, sollte dann in eine eigene funktion
        for (i in dataArray) {
            // get time
            val time = result.getDuration(i.starttime, i.endtime )



            tv_dynamic.append(
                   i.id.toString() +  ".  eintrag: " +System.getProperty ("line.separator")
                   + "note: " + i.note.toString() + " " +System.getProperty ("line.separator")
                   + "Duration: " + time + " " +System.getProperty ("line.separator")
                   + "start lat: " +  i.startlat.toString() +System.getProperty ("line.separator")
                   +"end lat: " +  i.startlat.toString() + System.getProperty ("line.separator")
                   + "Distance: +++"  + System.getProperty ("line.separator")
                   +System.getProperty ("line.separator")
            )

        }


        layout.addView(tv_dynamic)
*/

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



     override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, id: Long) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("id", id)
    //    startActivity(intent)
    }

    override fun onClick(v: View?) {
        val intent = Intent(this, ResultActivity::class.java)
      //  startActivity(intent)
    }

}