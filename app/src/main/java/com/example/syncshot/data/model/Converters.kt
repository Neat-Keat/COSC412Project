package com.example.syncshot.data.model

import androidx.room.TypeConverter

class Converters {

    //These two functions convert an IntArray into a primitive that the ROOM database can work with, and back again
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
	
	//These two functions convert a 2D IntArray and back again
	@TypeConverter
	fun from2dIntArray(value: Array<IntArray>): String {
		var temp: String = ""
		for(i in 0 until value.size) {
			temp += value[i].joinToString(",")
			if (i != value.size - 1) {temp += ";"}
		}
		return temp
	}
	
	@TypeConverter
	fun to2dIntArray(value: String): Array<IntArray> {
	if (value.isEmpty()) {
		// Return a single-element array consisting of an IntArray of 18 0's
    	return Array(1) {IntArray(18){ 0 }}
    } else if (value.count {it == ';'} == 0) {
		// if no splitting needed, just return single-row 2D Array containing score array
    	return Array(1) {toIntArray(value)}
	} else {
        val tempStrArray: Array<String> = value.split(";").toTypedArray()
        val int2dArray = Array(tempStrArray.size){IntArray(1){0}}
        for(i in 0 until tempStrArray.size) {
            int2dArray[i] = tempStrArray[i].split(",").map { it.toInt() }.toIntArray()
        }
        return int2dArray
		}
	}

	// these two functions convert a String array and back again
	@TypeConverter
	fun fromStringArray(value: Array<String>): String {
		// Convert a Array<String> into a single string
		return value.joinToString(",")
	}
	
	@TypeConverter
	fun toStringArray(value: String): Array<String> {
	return if (value.isEmpty()) {
            Array<String>(1) { "Nobody" } // Return only the "name" Nobody if empty
        } else {
            value.split(",").toTypedArray()
        }
	}
}
