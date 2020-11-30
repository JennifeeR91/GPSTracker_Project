package com.gpstracker.gpstracker_project.activity

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

        Toast.makeText(this,
            R.string.test, Toast.LENGTH_LONG).show()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)



        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    Toast.makeText(this,
                        R.string.logged_in, Toast.LENGTH_LONG).show()

                    // Respond to navigation item 1 click
                    true
                }
                R.id.page_2 -> {
                    Toast.makeText(this,
                        R.string.logged_in, Toast.LENGTH_LONG).show()

                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }

    }



}