package com.bucket.list.bucketlist.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.databinding.FragmentRegistrationBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class RegistrationFragment : Fragment() {
    lateinit var binding : FragmentRegistrationBinding
    lateinit var sharedPreferences: SharedPreferences
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.singUp.setOnClickListener {
            putData()
        }

        binding.auth.setOnClickListener {
            findNavController().navigate(R.id.authorizationFragment)
        }

        sharedPreferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)

    }

    private fun putData() {
        val name = binding.name.text.toString()
        val login = binding.login.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        val user = hashMapOf(
            "name" to name,
            "login" to "@$login",
            "email" to email,
            "password" to password
        )

        db.collection("users")
            .whereEqualTo("login", "@$login")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            sharedPreferences.edit().putString("name", name).apply()
                            sharedPreferences.edit().putString("login", login).apply()
                            sharedPreferences.edit().putString("email", email).apply()
                            sharedPreferences.edit().putString("password", password).apply()

                            findNavController().navigate(R.id.homeFragment)
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                        }
                } else {
                    Toast.makeText(requireContext(), "Такой логин уже сущестует", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error checking for existing login", exception)
                Toast.makeText(requireContext(), "Ошибка $exception", Toast.LENGTH_SHORT).show()
            }
    }
}