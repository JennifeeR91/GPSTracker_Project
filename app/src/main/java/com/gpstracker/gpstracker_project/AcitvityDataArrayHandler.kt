package com.gpstracker.gpstracker_project


val data: MutableList<String> = ArrayList()

class ActivityDataArrayHandler {

    //create array for data to save in


    // instert into array
    fun insertData(savestring:String ) {
        data.add(savestring)

        // ausgabe zum testen
        println("insert this data in data array: ")
        for (i in data) {
            println(i)
        }
        println("--- --- --- ---")
        return
    }

    // deliver data
    fun get(): MutableList<String> {
        // keine ahnung warum man das braucht, aber ohne der vorherigen verwendung kommt nix zurück
        data.add("")
        println("get aufgerufen - daten kommen")

        return data
    }


    // deliver data
    fun del() {
        data.clear()
        return
    }



}
