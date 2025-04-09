package com.example.syncshot.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Query("SELECT * FROM players ORDER BY id ASC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Query("DELETE FROM players")
    suspend fun clearAll()
}
