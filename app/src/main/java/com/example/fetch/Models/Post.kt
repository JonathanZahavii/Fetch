package com.example.fetch.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Post(
    val postId: String = UUID.randomUUID().toString(),
    val caption: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val petName: String = "",
    val timestamp: Long = 0,
    val userId: String = "",
    val postType: PostType = PostType.OTHER
) : Parcelable