package com.gpstracker.gpstracker_project.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.gpstracker.gpstracker_project.Activity
import com.gpstracker.gpstracker_project.ActivityAdapter
import com.gpstracker.gpstracker_project.Database
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.history_activity.*
import kotlinx.android.synthetic.main.result_activity.tvPageTitle


class HistoryActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    // new instance of Database
    private val db = Database(this)

    //initialize activityAdapter
    private var activityAdapter: ActivityAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity)
        tvPageTitle.setText(R.string.history)

        // show Activity List
        val activities: List<Activity> = db.getAllActivities()
        activityAdapter = ActivityAdapter(this, activities)
        lvActivities.adapter = activityAdapter
        lvActivities.onItemClickListener = this

        // Bottom Navigation
        showBottomNavigation()

    }

    private fun showBottomNavigation() {
        //set current as active in navigation
        bottom_navigation.getMenu().findItem(R.id.history_page).setChecked(true)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
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
        val intent = Intent(this, DetailViewActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }


}