package com.gpstracker.gpstracker_project.activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gpstracker.gpstracker_project.Preferences
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.profile_activity.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private val preferences = Preferences()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)
        tvPageTitle.text = "Profile"

        btnLogout.setOnClickListener(this)
        val email = preferences.showEmail(this)
        tvEmail.text = email



        // Bottom Naviagation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        //set current as active in navigation
        bottomNavigationView.getMenu().findItem(R.id.profile_page).setChecked(true);
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


    override fun onClick(v: View?) {
        // Display Toast
        Toast.makeText(this, R.string.logged_out, Toast.LENGTH_LONG).show()

        // Go to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Save login state
        preferences.setUserLoggedIn(this, false, "")

        // Finish MainActivity
        finish()
    }

}

