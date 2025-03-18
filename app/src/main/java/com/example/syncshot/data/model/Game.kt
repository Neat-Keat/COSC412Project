package com.example.syncshot.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

//This is the custom data type we are using to store info related to every golf game entry
@Entity(tableName = "games")
@TypeConverters(androidx.databinding.adapters.Converters::class)
data class Game(
    @PrimaryKey
    val id: String,         //Unique identifier for each game, kind of like an index for the database
    val location: String,   //Stores the location of where the game took place
    val date: String,       //Stores the date that the game was played
    val finalScore: Int,    //Stores the final score (this should be calculated from our OCR model!!)
    val holes: IntArray     //an int array of size 18, which can store stroke info on every hole in a course
)