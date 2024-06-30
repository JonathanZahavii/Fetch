package com.example.fetch.modules.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fetch.Models.Post
import com.example.fetch.databinding.ItemPostBinding
import com.example.fetch.databinding.ItemPostBinding.inflate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.recyclerview.widget.DiffUtil
import com.squareup.picasso.Picasso

class PostAdapter : ListAdapter<Post, PostAdapter.PostViewHolder>(PostViewHolder.PostDiffCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.tvCaption.text = post.caption
            binding.tvLocation.text = post.location
            binding.tvPetName.text = post.petName
            binding.tvTimestamp.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                Date(post.timestamp)
            )
            // Load image using an image loading library like Glide or Picasso
            Picasso.get().load(post.imageUrl).into(binding.ivImage)  // NEW CODE        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}}

