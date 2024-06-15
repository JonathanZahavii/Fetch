package com.example.fetch.Modules.Feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fetch.databinding.FragmentFeedBinding
import com.example.fetch.models.PostType

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
