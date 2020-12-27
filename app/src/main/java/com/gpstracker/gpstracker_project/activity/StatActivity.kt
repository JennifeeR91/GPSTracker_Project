package com.gpstracker.gpstracker_project.activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.history_activity.bottom_navigation
import kotlinx.android.synthetic.main.history_activity.tvPageTitle
import kotlinx.android.synthetic.main.stat_activity.*


class StatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stat_activity)
        // show title
        tvPageTitle.setText(R.string.statistics)
        tvPageUpcoming.setText(R.string.coming_soon)

        // Bottom Navigation
        showBottomNavigation()
    }

    private fun showBottomNavigation() {
        //set current as active in navigation
        bottom_navigation.menu.findItem(R.id.stats_page).isChecked = true

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


}

