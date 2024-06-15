package com.example.fetch.Modules.AddPost

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fetch.R
import com.example.fetch.databinding.FragmentAddPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    imageUri = result.data!!.data
                    binding.ivSelectedImage.setImageURI(imageUri)
                    binding.ivSelectedImage.visibility = View.VISIBLE
                }
            }

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.btnAddPost.setOnClickListener {
            val petName = binding.etPetName.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val caption = binding.etCaption.text.toString().trim()

            if (petName.isEmpty() || location.isEmpty() || caption.isEmpty() || imageUri == null) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show the progress overlay
            binding.progressOverlay.visibility = View.VISIBLE

            uploadPost(petName, location, caption)
        }
    }

    private fun uploadPost(petName: String, location: String, caption: String) {
        val storageRef = storage.reference.child("posts/${UUID.randomUUID()}")
        imageUri?.let {
            storageRef.putFile(it)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        savePostToFirestore(petName, location, caption, uri.toString())
                    }
                }
                .addOnFailureListener {
                    // Hide the progress overlay
                    binding.progressOverlay.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Image upload failed: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            // Hide the progress overlay
            binding.progressOverlay.visibility = View.GONE
            Toast.makeText(context, "Image URI is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePostToFirestore(
        petName: String,
        location: String,
        caption: String,
        imageUrl: String
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Hide the progress overlay
            binding.progressOverlay.visibility = View.GONE
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val post = hashMapOf(
            "petName" to petName,
            "location" to location,
            "caption" to caption,
            "imageUrl" to imageUrl,
            "userId" to currentUser.uid,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("posts").add(post)
            .addOnSuccessListener {
                // Hide the progress overlay
                binding.progressOverlay.visibility = View.GONE
                Toast.makeText(context, "Post added successfully", Toast.LENGTH_SHORT).show()
                // Navigate back to feed
                findNavController().navigate(R.id.action_addPost_to_feedFragment)
            }
            .addOnFailureListener {
                // Hide the progress overlay
                binding.progressOverlay.visibility = View.GONE
                Toast.makeText(context, "Post upload failed: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}