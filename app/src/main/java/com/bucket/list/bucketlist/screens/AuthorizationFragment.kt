package com.bucket.list.bucketlist.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.databinding.FragmentAuthorizationBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class AuthorizationFragment : Fragment() {
    lateinit var binding : FragmentAuthorizationBinding
    lateinit var sharedPreferences: SharedPreferences
    val db = Firebase.firestore
    var name = ""
    var email = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthorizationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)

        binding.save.setOnClickListener {
            getData()
        }

        binding.reg.setOnClickListener {
            findNavController().navigate(R.id.registrationFragment)
        }
    }

    private fun getData(){

        val login = binding.login.text.toString()
        val password = binding.password.text.toString()

        if (login.isEmpty()) {
            Toast.makeText(requireContext(), "Email не указан", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users")
            .whereEqualTo("login", "@$login")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (document in result) {
                    val storagePassword = document.getString("password")
                    if (storagePassword == password) {
                        db.collection("users")
                            .whereEqualTo("login", "@$login")
                            .get()
                            .addOnSuccessListener { results ->
                                for (document in result){
                                    name = document.get("name").toString()
                                    email = document.get("email").toString()
                                }

                                sharedPreferences.edit().putString("login", login).apply()
                                sharedPreferences.edit().putString("password", password).apply()
                                sharedPreferences.edit().putString("name", name).apply()
                                sharedPreferences.edit().putString("email", email).apply()

                                findNavController().navigate(R.id.homeFragment)
                            }

                    } else {
                        Toast.makeText(requireContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Неудачно: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

    }

}