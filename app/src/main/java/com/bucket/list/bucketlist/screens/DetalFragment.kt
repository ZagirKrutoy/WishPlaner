package com.bucket.list.bucketlist.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.databinding.FragmentDetalBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class DetalFragment : Fragment() {
    lateinit var binding: FragmentDetalBinding
    lateinit var sharedPreferences: SharedPreferences

    private var imageUri: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val PICK_IMAGE_REQUEST = 1

    private var login = ""
    private var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
        login = sharedPreferences.getString("login", "").toString()
        name = sharedPreferences.getString("name", "").toString()

        val title = arguments?.getString("title")

        binding.title.text = title

        binding.share.setOnClickListener {
            if (imageUri != null) {
                uploadImageAndPutData()
            } else {
                putData("")
            }
        }

        binding.delete.setOnClickListener {
            deleteItemFromFirestore(binding.title.text.toString())
        }

        binding.image.setOnClickListener {
            openGallery()
        }

        binding.text.setOnClickListener {
            openGallery()
        }

        navigation()
    }

    private fun navigation(){

        binding.profile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        binding.home.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

    }

    private fun deleteItemFromFirestore(title: String) {
        db.collection("goals")
            .whereEqualTo("title", title)
            .whereEqualTo("login", "@$login")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Удален", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Ошибка при удалении документа", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Ошибка при получении документов", exception)
            }
    }

    private fun putData(imageUrl: String) {
        val goal = hashMapOf(
            "title" to binding.title.text.toString(),
            "login" to login,
            "name" to name,
            "image" to imageUrl
        )

        db.collection("generalGoals")
            .add(goal)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireContext(), "Добавлено", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

    private fun uploadImageAndPutData() {
        if (imageUri != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val storageRef = storage.reference.child("images/$fileName")

            storageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        putData(imageUrl)
                    }
                        .addOnFailureListener { exception ->
                            Log.e("TAG", "Error getting download URL: ${exception.message}")
                            putData("")  // Fallback to empty string if download URL failed
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    putData("")  // Fallback to empty string if upload fails
                }
        } else {
            putData("")  // No image selected, use empty string
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.image.setImageURI(imageUri)
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST)
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }
}