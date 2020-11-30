package com.gpstracker.gpstracker_project.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gpstracker.gpstracker_project.R
import kotlinx.android.synthetic.main.current_activity.*


class CurrentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.current_activity)

        Toast.makeText(this, R.string.test, Toast.LENGTH_LONG).show()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        //set current as active in navigation
        //bottomNavigationView.selectedItemId(R.id.page_1)

        bottomNavigationView.getMenu().findItem(R.id.activity_page).setChecked(true);




        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.activity_page -> {
                    // Go to CurrentActivity
                    val intent1 = Intent(this, CurrentActivity::class.java)
                    startActivity(intent1)
                    // Finish MainActivity
                    finish()
                    true
                }
                R.id.history_page -> {
                    Toast.makeText(this,  "history page here", Toast.LENGTH_LONG).show()
                    // Go to HistoryActivity
                    val intent2 = Intent(this, HistoryActivity::class.java)
                    startActivity(intent2)
                    // Finish MainActivity
                    finish()
                    // Respond to navigation item 2 click
                    true
                }
                R.id.stats_page -> {
                    Toast.makeText(this,"Statistics here", Toast.LENGTH_LONG).show()
                    // Go to StatActivity
                    val intent = Intent(this, StatActivity::class.java)
                    startActivity(intent)
                    // Finish MainActivity
                    finish()
                    true
                }
                R.id.profile_page -> {
                    Toast.makeText(this, "Profile page here", Toast.LENGTH_LONG).show()
                    // Go to ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    //Finish MainActivity
                    finish()
                    true
                }
                else -> false
            }
        }

    }



}