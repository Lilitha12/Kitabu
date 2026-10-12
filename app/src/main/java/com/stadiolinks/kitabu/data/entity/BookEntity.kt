package com.stadiolinks.kitabu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bookId: Long = 0,
    val title: String,
    val author: String,
    val category: String,
    val isAvailable: Boolean = true,
    val imageRes: String = ""
)