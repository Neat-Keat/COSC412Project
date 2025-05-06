package com.example.syncshot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

//This is the custom data type we are using to store info related to every golf game entry
@Entity(tableName = "games")
@TypeConverters(Converters::class)

//Primary Constructor takes, in order: id, names array, strokes 2D array, par array, date (put null if user doesn't provide), location (put null if user doesn't provide)
data class Game(
    @PrimaryKey
    val id: String,         //Unique identifier for each game, kind of like an index for the database
    var names: Array<String>,       //Stores each player's name
    var strokes: Array<IntArray>,   //Stores an int array of shots taken on each hole, for each player by index
	var par: IntArray,      //Stores the Par for each hole
	var date: String?,      //Stores the date that the game was played. can be null, for if user gives nothing
    var location: String?   //Stores the location of where the game took place. can be null too
) {
    var scores = Array(strokes.size){IntArray(18){0}}    //Stores the compared-to-par scores of each player, for each hole.  Initialized to all 0s, as though a player stroked Par every hole
    var finalScores = IntArray(strokes.size){0}          //Stores the final scores of each player
    init {
        for (p in scores.indices) {
    		for(i in 0..17) {
                //if the number of strokes on a given hole is <= 0 (e.g. an invalid number) then it assumes they got Par on that hole
    			if (strokes[p][i] > 0) {
                    scores[p][i] = strokes[p][i] - par[i]
                }
    		}
    	}
    	for (p in finalScores.indices) {
    		finalScores[p] = scores[p].sum()
    	}
    }
}
