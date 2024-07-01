package com.example.fetch.Modules.Feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                postAdapter.submitList(posts) 
            } 
            .addOnFailureListener { exception -> 
                // Handle the error 
                Log.e("FeedFragment", "Error getting documents: ", exception) 
            } 
    } 

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
