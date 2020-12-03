package com.gpstracker.gpstracker_project

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import java.sql.Timestamp

class Preferences {

    private val PREFERENCES_USER_LOGGED_IN = "user_logged_in"
    private val PREFERENCES_NAME = "preferences_note"

    fun isUserLoggedIn(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        return preferences.getBoolean(PREFERENCES_USER_LOGGED_IN, false)
    }

    fun setUserLoggedIn(context: Context, loggedIn: Boolean, email: String) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        preferences.edit().putBoolean(PREFERENCES_USER_LOGGED_IN, loggedIn).apply()
        preferences.edit().putString("email", email).apply()
    }

    fun showEmail(context: Context): String? {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        return preferences.getString("email", "default value")

    }

    fun setStartLocation(context: Context, timstamp:String, lat:String, long:String ) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        preferences.edit().putString("timestmp", timstamp).apply()
        preferences.edit().putString("lat", lat).apply()
        preferences.edit().putString("long", long).apply()

    }

    fun getLocations(context: Context): String? {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val long = preferences.getString("lat", "leerer Wert lat")
        val lat = preferences.getString("long", "leerer Wert long")
        val time = preferences.getString("timestmp", "leerer Wert time")
        return time + ": " + long  + ",  " + lat

    }


}