package com.gpstracker.gpstracker_project

data class Activity(val id: Long, val startlong: Double, var endlong: Double, val startlat: Double, var endlat: Double, var starttime: Long,
                    var endtime: Long, var note: String, var deleted: Boolean)
