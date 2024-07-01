package com.example.fetch.Modules.AddPost

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.navigation.fragment.navArgs
import com.example.fetch.Models.Comment
import com.example.fetch.R
import com.example.fetch.databinding.FragmentAddPostBinding
import com.example.fetch.Models.PostType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.UUID

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null

    private val args: AddPostFragmentArgs by navArgs()

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedDateTime: Calendar? = null

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

        val postType = PostType.valueOf(args.postType)
        if (postType == PostType.PLAYDATE) {
            binding.dateTimeLayout.visibility = View.VISIBLE
        } else {
            binding.dateTimeLayout.visibility = View.GONE
        }

        binding.btnPickDateTime.setOnClickListener {
            showDateTimePicker()
        }

        binding.btnAddPost.setOnClickListener {
            val petName = binding.etPetName.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val caption = binding.etCaption.text.toString().trim()

            if (petName.isEmpty() || location.isEmpty() || caption.isEmpty() || imageUri == null ||
                (postType == PostType.PLAYDATE && selectedDateTime == null)
            ) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show the progress overlay
            binding.progressOverlay.visibility = View.VISIBLE

            uploadPost(petName, location, caption, postType, selectedDateTime)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_addPost_to_feedFragment)
        }
    }

    private fun showDateTimePicker() {
        val currentDateTime = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        selectedDateTime = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth, hourOfDay, minute)
                        }
                        binding.tvDateTime.text =
                            "${dayOfMonth}/${month + 1}/${year} ${hourOfDay}:${minute}"
                    },
                    currentDateTime.get(Calendar.HOUR_OF_DAY),
                    currentDateTime.get(Calendar.MINUTE),
                    true
                ).show()
            },
            currentDateTime.get(Calendar.YEAR),
            currentDateTime.get(Calendar.MONTH),
            currentDateTime.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun uploadPost(
        petName: String,
        location: String,
        caption: String,
        postType: PostType,
        dateTime: Calendar?
    ) {
        val storageRef = storage.reference.child("posts/${UUID.randomUUID()}")
        imageUri?.let {
            storageRef.putFile(it)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        savePostToFirestore(
                            petName,
                            location,
                            caption,
                            postType,
                            dateTime,
                            uri.toString(),
                        )
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
        postType: PostType,
        dateTime: Calendar?,
        imageUrl: String,
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Hide the progress overlay
            binding.progressOverlay.visibility = View.GONE
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val initLikes = 0
        val initComments = emptyList<Comment>()

        val post = hashMapOf(
            "petName" to petName,
            "location" to location,
            "caption" to caption,
            "imageUrl" to imageUrl,
            "userId" to currentUser.uid,
            "timestamp" to System.currentTimeMillis(),
            "postType" to postType,
            "dateTime" to dateTime?.timeInMillis,
            "likes" to initLikes,
            "comments" to initComments
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
