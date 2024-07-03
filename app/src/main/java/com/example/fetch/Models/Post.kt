package com.example.fetch.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey var postId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "caption") var caption: String = "",
    @ColumnInfo(name = "imageUrl") var imageUrl: String = "",
    @ColumnInfo(name = "location") var location: String = "",
    @ColumnInfo(name = "petName") var petName: String = "",
    @ColumnInfo(name = "timestamp") var timestamp: Long = 0,
    @ColumnInfo(name = "userId") var userId: String = "",
    @ColumnInfo(name = "postType") var postType: PostTypes = PostTypes.OTHER
)