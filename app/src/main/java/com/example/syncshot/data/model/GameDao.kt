package com.example.syncshot.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


//The interface that provides methods for interacting with our database (inserting, querying, updating, deleting)
@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameById(id: String): Game?

    @Update
    suspend fun updateGame(game: Game)

    //TODO: add delete functionality
    @Delete
    suspend fun delete(game: Game): Int

    @Query("SELECT * FROM games")
    suspend fun getAllGames(): List<Game>
}