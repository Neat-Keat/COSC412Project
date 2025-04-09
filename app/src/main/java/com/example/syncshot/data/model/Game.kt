package com.example.syncshot.data.model
import com.example.syncshot.data.model.Converters

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

//This is the custom data type we are using to store info related to every golf game entry
@Entity(tableName = "games")
@TypeConverters(Converters::class)
data class Game(
    @PrimaryKey
    val id: String,         //Unique identifier for each game, kind of like an index for the database
    val location: String,   //Stores the location of where the game took place
    val date: String,       //Stores the date that the game was played
    val names: Array<String>,     //Stores each player's name by index
    val holes: Array<IntArray>,   //Stores an int array of scores for each player
    val finalScores: IntArray     //Stores the final scores of each player
)
