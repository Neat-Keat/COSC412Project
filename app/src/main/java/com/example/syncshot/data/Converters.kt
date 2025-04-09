package com.example.syncshot.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromScoreList(scores: List<Int>): String = scores.joinToString(",")

    @TypeConverter
    fun toScoreList(data: String): List<Int> =
        if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }
}
