package com.example.fetch.Modules.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fetch.Models.Post
import com.example.fetch.Models.PostTypes
import com.example.fetch.databinding.ItemPostBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            if (post.postType == PostTypes.PLAYDATE.name) {
                binding.tvPlaydateWith.visibility = View.VISIBLE
                binding.tvPetName.text = post.petName // Set pet name as usual

                binding.btnJoin.visibility = View.VISIBLE
            } else {
                binding.tvPlaydateWith.visibility = View.GONE
                binding.btnJoin.visibility = View.GONE
            }

            // Load image using an image loading library like Glide or Picasso
            Picasso.get().load(post.imageUrl).into(binding.ivImage)
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
