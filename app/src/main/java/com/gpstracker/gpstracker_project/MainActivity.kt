package com.gpstracker.gpstracker_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

//test
//test 1 worked

class MainActivity : AppCompatActivity(), View.OnClickListener  {

    private val preferences = Preferences()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        loginButton.setOnClickListener(this)


        // Don't show MainActivity when user is already logged in
        if (preferences.isUserLoggedIn(this)) {
            // Go to CurrentActivity
            val intent = Intent(this, CurrentActivity::class.java)
            startActivity(intent)

            // Finish MainActivity
            finish()
        }

    }


    override fun onClick(v: View?) {
        // Display Toast
        Toast.makeText(this, R.string.logged_in, Toast.LENGTH_LONG).show()

        // Go to CurrentActivity
        val intent = Intent(this, CurrentActivity::class.java)
        startActivity(intent)

        // Save login state
        preferences.setUserLoggedIn(this, true)

        // Finish MainActivity
        finish()
    }
}