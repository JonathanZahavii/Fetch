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
import com.example.fetch.modules.Adapters.PostAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance() // NEW CODE
    private lateinit var postAdapter: PostAdapter // NEW CODE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddPost.setOnClickListener {
            val action =
                FeedFragmentDirections.actionFeedFragmentToAddPostFragment(PostType.SINGLE.name)
            findNavController().navigate(action)
        }

        binding.btnAddPlaydate.setOnClickListener {
            val action =
                FeedFragmentDirections.actionFeedFragmentToAddPostFragment(PostType.PLAYDATE.name)
            findNavController().navigate(action)
        }

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_profileFragment)
        }

        setupRecyclerView() // NEW CODE
        loadPosts() // NEW CODE
    }

    private fun setupRecyclerView() { // NEW CODE
        postAdapter = PostAdapter() // NEW CODE
        binding.recyclerView.apply { // NEW CODE
            layoutManager = LinearLayoutManager(context) // NEW CODE
            adapter = postAdapter // NEW CODE
        } // NEW CODE
    } // NEW CODE

    private fun loadPosts() { // NEW CODE
        db.collection("posts") // NEW CODE
            .orderBy("timestamp", Query.Direction.DESCENDING) // NEW CODE
            .get() // NEW CODE
            .addOnSuccessListener { result -> // NEW CODE
                val posts = result.toObjects(Post::class.java) // NEW CODE
                postAdapter.submitList(posts) // NEW CODE
            } // NEW CODE
            .addOnFailureListener { exception -> // NEW CODE
                // Handle the error // NEW CODE
                Log.e("FeedFragment", "Error getting documents: ", exception) // NEW CODE
            } // NEW CODE
    } // NEW CODE

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
