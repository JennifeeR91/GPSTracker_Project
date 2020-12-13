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
        private const val DATABASE_TABLE_NAME_POINTS = "points"
        private const val DATABASE_VERSION = 3

        // Database activity table column names
        private const val KEY_ID = "id"
        private const val KEY_STARTLONG = "startlongitude"
        private const val KEY_ENDLONG = "endlongitude"
        private const val KEY_STARTLAT = "startlatitude"
        private const val KEY_ENDLAT = "endlatitude"
        private const val KEY_STARTTIME = "starttime"
        private const val KEY_ENDTIME = "endtime"
        private const val KEY_NOTE = "note"
        private const val KEY_DELETED = "deleted"

        // Database create avtivity table statement
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


        // Database points table column names
        private const val TIMESTMP = "timestmp"
        private const val LONG = "long"
        private const val LAT = "lat"

        // Database create points table statement
        private const val CREATE_TABLE_POINTS = ("""CREATE TABLE $DATABASE_TABLE_NAME_POINTS(
                $KEY_ID  INT,
                $TIMESTMP INT,
                $LONG FLOAT,
                $LAT FLOAT
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
        private const val DROP_TABLE_POINTS = "DROP TABLE IF EXISTS $DATABASE_TABLE_NAME_POINTS"

        // Database select all statement
        private const val SELECT_ALL = "SELECT * FROM $DATABASE_TABLE_NAME WHERE $KEY_DELETED = 0 ORDER BY id DESC"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
        db.execSQL(CREATE_TABLE_POINTS)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL(DROP_TABLE)
        db.execSQL(DROP_TABLE_POINTS)

        db.execSQL(CREATE_TABLE)
        db.execSQL(CREATE_TABLE_POINTS)
    }

    // Get all activities from database
    fun getAllActivities(): List<Activity> {
        val activities = ArrayList<Activity>()
        val cursor = readableDatabase.rawQuery(SELECT_ALL, null)
        cursor.moveToFirst().run {
            do {
                cursorToActivity(cursor)?.let {
                    activities.add(it)
                }
            } while (cursor.moveToNext())
        }

        readableDatabase.close()

        return activities
    }

    // Insert activity into database
    fun insertActivity(activity: Activity): Long {
        val values = activityToContentValues(activity)

        return writableDatabase.insert(DATABASE_TABLE_NAME, null, values)
    }

    // Get single activity from database
    fun getActivity(id: Long): Activity? {
        val activity: Activity?
        val cursor = readableDatabase.query(
            DATABASE_TABLE_NAME, CURSOR_ARRAY, "$KEY_ID=?",
            arrayOf(id.toString()), null, null, null, null
        )

        cursor.moveToFirst()
        activity = cursorToActivity(cursor)
        cursor.close()

        return activity
    }

    private fun cursorToActivity(cursor: Cursor): Activity? {
        var activity: Activity? = null
        if (cursor?.count == 0) return null
        cursor.run {
            activity = Activity(
                getLong(getColumnIndex(KEY_ID)),
                getDouble(getColumnIndex(KEY_STARTLONG)),
                getDouble(getColumnIndex(KEY_ENDLONG)),
                getDouble(getColumnIndex(KEY_STARTLAT)),
                getDouble(getColumnIndex(KEY_ENDLAT)),
                getLong(getColumnIndex(KEY_STARTTIME)),
                getLong(getColumnIndex(KEY_ENDTIME)),
                getString(getColumnIndex(KEY_NOTE)),
                getInt(getColumnIndex(KEY_DELETED)) > 0
            )
        }
        return activity
    }

    // Update single activity
    fun updateNote(activity: Activity): Int {
        return writableDatabase.update(
            DATABASE_TABLE_NAME,
            activityToContentValues(activity),
            "$KEY_ID=?",
            arrayOf(activity.id.toString())
        )
    }

    // Set  single activity Deleted
    fun delActivity(activity: Activity): Int {
        activity.deleted = true
        return writableDatabase.update(
            DATABASE_TABLE_NAME,
            activityToContentValues(activity),
            "$KEY_ID=?",
            arrayOf(activity.id.toString())
        )
    }


    // Create new ContentValues object from Activity
    private fun activityToContentValues(activity: Activity): ContentValues {
        val values = ContentValues()

        values.put(KEY_STARTLONG, activity.startlong)
        values.put(KEY_ENDLONG, activity.endlong)
        values.put(KEY_STARTLAT, activity.startlat)
        values.put(KEY_ENDLAT, activity.endlat)
        values.put(KEY_STARTTIME, activity.starttime)
        values.put(KEY_ENDTIME, activity.endtime)
        values.put(KEY_NOTE, activity.note)
        values.put(KEY_DELETED, activity.deleted)

        return values
    }


    //get ID from last entry in activity DB
    fun getLastActivityID(): Long {
        val query = "SELECT ROWID from $DATABASE_TABLE_NAME order by ROWID DESC limit 1"
        val c: Cursor = readableDatabase.rawQuery(query, null)
        var lastId: Long = 0

        if (c != null && c.moveToFirst()) {
            lastId = c.getLong(0) //The 0 is the column index, we only have 1 column, so the index is 0
        }

        return lastId
    }

    fun insertPosition(activityId:Long, time:Long, long:Double, lat:Double): Long {
        val values = ContentValues()
        values.put(KEY_ID, activityId)
        values.put(TIMESTMP, time)
        values.put(LONG, long)
        values.put(LAT, lat)

        return writableDatabase.insert(DATABASE_TABLE_NAME_POINTS, null, values)
    }

    fun getPoints(id: Long): ArrayList<String> {
        val points: ArrayList<String> = ArrayList()

        val cursor = readableDatabase.query(
            DATABASE_TABLE_NAME_POINTS, arrayOf(KEY_ID, TIMESTMP, LAT, LONG), "$KEY_ID=?",
            arrayOf(id.toString()), null, null, null, null
        )
        println("cursor: " + cursor)
        cursor.moveToFirst().run {
            do {
                // hier sollte ein verschachteltes array zurückkommen, jeder punkt ist ein array
                val p = arrayListOf<Any>()
                var timestamp = cursor.getInt(1).toString()
                var lat = cursor.getDouble(2).toString()
                var long = cursor.getDouble(3).toString()

                p.add(timestamp + " " + lat + " " + long  )

                var string= timestamp + " " + lat + " " + long

                points.add(string)

            } while (cursor.moveToNext())


        }
        readableDatabase.close()

        return points
    }

}