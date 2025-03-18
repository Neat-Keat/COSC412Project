package com.example.syncshot.data.model

import androidx.room.TypeConverter

class Converters {

    //This class converts the holes IntArray[] into a primitive that our ROOM database can work with, and back again!
    @TypeConverter
    fun fromIntArray(value: IntArray): String {
        // Convert the IntArray to a string representation
        return value.joinToString(",")
    }

    @TypeConverter
    fun toIntArray(value: String): IntArray {
        // Convert the string representation back to an IntArray
        return if (value.isEmpty()) {
            IntArray(18) { 0 } // Return an array of 18 zeros if the string is empty
        } else {
            value.split(",").map { it.toInt() }.toIntArray()
        }
    }
}