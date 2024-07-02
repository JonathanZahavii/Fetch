package com.example.fetch.Modules.Feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fetch.Models.Post
import com.example.fetch.R
import com.example.fetch.databinding.FragmentFeedBinding
import com.example.fetch.Models.PostType
import com.example.fetch.Modules.Adapters.PostAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance() 
    private lateinit var postAdapter: PostAdapter
    private var allPosts: List<Post> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.btnAddPost.setOnClickListener {
            val action =
                FeedFragmentDirections.actionFeedFragmentToAddPostFragment(PostType.SINGLE.name)
            findNavController().navigate(action)
        }

        binding.toolbar.btnAddPlaydate.setOnClickListener {
            val action =
                FeedFragmentDirections.actionFeedFragmentToAddPostFragment(PostType.PLAYDATE.name)
            findNavController().navigate(action)
        }

        binding.toolbar.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_profileFragment)
        }

        setupRecyclerView()
        loadPosts()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchPosts(query!!)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    searchPosts(newText!!)
                } else {
                    postAdapter.submitList(allPosts) // Reset to all posts if search bar is empty
                }
                return false
            }
        })
    }

    private fun setupRecyclerView() { 
        postAdapter = PostAdapter() 
        binding.recyclerView.apply { 
            layoutManager = LinearLayoutManager(context) 
            adapter = postAdapter 
        } 
    }

    private fun loadPosts() {
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.toObjects(Post::class.java)
                allPosts = posts
                postAdapter.submitList(posts) // Initial data set
            }
            .addOnFailureListener { exception ->
                // Handle the error
                Log.e("FeedFragment", "Error getting documents: ", exception)
            }
    }

    private fun searchPosts(query: String) {
        val filteredPosts = allPosts.filter { post ->
            // Search logic - check caption, location, and pet name
            post.caption?.contains(query, ignoreCase = true) ?: false ||
                    post.location?.contains(query, ignoreCase = true) ?: false ||
                    post.petName?.contains(query, ignoreCase = true) ?: false
        }
        postAdapter.submitList(filteredPosts) // Update adapter with filtered data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
