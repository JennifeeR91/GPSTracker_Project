package com.gpstracker.gpstracker_project

import android.content.Context

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
        val  email: String = preferences.getString("email", "default value").toString()
        return email

    }

}