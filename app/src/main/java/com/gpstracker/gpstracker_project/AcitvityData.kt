package com.gpstracker.gpstracker_project

import android.content.Context


val data: MutableList<String> = ArrayList()

class AcitvityData {

    //create array for data to save in


    // instert into array
    fun insterData(savestring:String ) {

        data.add(savestring)

        // ausgabe zum testen
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
        println("hier kommen jetzt die Daten, achtung")

        return data
    }



}
