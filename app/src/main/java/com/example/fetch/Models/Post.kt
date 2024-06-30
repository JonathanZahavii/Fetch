package com.example.fetch.Models

data class Post(
    val caption: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val petName: String = "",
    val timestamp: Long = 0,
    val userId: String = ""
)