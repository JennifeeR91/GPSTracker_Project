package com.gpstracker.gpstracker_project

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        // Database properties
        private const val DATABASE_NAME = "gpstracker"
        private const val DATABASE_TABLE_NAME = "activities"
        private const val DATABASE_VERSION = 1

        // Database table column names
        private const val KEY_ID = "id"
        private const val KEY_STARTLONG = "startlongitude"
        private const val KEY_ENDLONG = "endlongitude"
        private const val KEY_STARTLAT = "startlatitude"
        private const val KEY_ENDLAT = "endlatitude"
        private const val KEY_STARTTIME = "starttime"
        private const val KEY_ENDTIME = "endtime"
        private const val KEY_NOTE = "note"
        private const val KEY_DELETED = "deleted"

        // Database create table statement
        private const val CREATE_TABLE = ("""CREATE TABLE $DATABASE_TABLE_NAME(
                $KEY_ID INTEGER PRIMARY KEY,
                $KEY_STARTLONG FLOAT,
                $KEY_ENDLONG FLOAT,
                $KEY_STARTLAT FLOAT,
                $KEY_ENDLAT FLOAT,
                $KEY_STARTTIME INT,
                $KEY_ENDTIME INT,            
                $KEY_NOTE TEXT,
                $KEY_DELETED BOOL
            )""")

        // Database cursor array
        private val CURSOR_ARRAY = arrayOf(
                KEY_ID,
                KEY_STARTLONG,
                KEY_ENDLONG,
                KEY_STARTLAT,
                KEY_ENDLAT,
                KEY_STARTTIME,
                KEY_ENDTIME,
                KEY_NOTE,
                KEY_DELETED
        )

        // Drop table statement
        private const val DROP_TABLE = "DROP TABLE IF EXISTS $DATABASE_TABLE_NAME"

        // Database select all statement
        private const val SELECT_ALL = "SELECT * FROM $DATABASE_TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL(DROP_TABLE)
        db.execSQL(CREATE_TABLE)
    }

    // Get all activities from database
    fun getAllActivities() {
       // TODO class Activity & getallEntries

        readableDatabase.close()

    }

    // Insert activity into database
    fun insertActivity() {
        // TODO class insertActivity
    }

    // Get single activity from database
    fun getActivity(id: Long) {
      //  TODO
    }

    private fun cursorToNote(cursor: Cursor) {
        //  TODO
    }

    // Update single activity
    fun updateNote() {
        //  TODO
    }

    // Create new ContentValues object from Activity
    private fun noteToContentValues() {
        //  TODO

        //put values

    }

    // Delete single activity
    fun deleteNote() {
        //  TODO
        //set deleted flag to "true"
    }
}