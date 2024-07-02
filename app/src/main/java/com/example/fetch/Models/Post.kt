package com.example.fetch.Models

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "caption") var caption: String = "",
    @ColumnInfo(name = "imageUrl") var imageUrl: String = "",
    @ColumnInfo(name = "location") var location: String = "",
    @ColumnInfo(name = "petName") var petName: String = "",
    @ColumnInfo(name = "timestamp") var timestamp: Long = 0,
    @ColumnInfo(name = "userId") var userId: String = "",
    @ColumnInfo(name = "postType") var postType: String = ""
)