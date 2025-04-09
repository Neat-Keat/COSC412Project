package com.example.syncshot.data

import androidx.room.*

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val scores: List<Int>
)
