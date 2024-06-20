package com.bucket.list.bucketlist.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.adapters.GoalHomeAdapter
import com.bucket.list.bucketlist.databinding.FragmentHomeBinding
import com.bucket.list.bucketlist.models.GoalHomeModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class HomeFragment : Fragment() {
    lateinit var binding : FragmentHomeBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter : GoalHomeAdapter
    lateinit var rv :RecyclerView
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("TY", 9).apply()

        rv = binding.rv
        adapter = GoalHomeAdapter()
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        getData()
        navigation()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getData(){
        db.collection("generalGoals")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.get("name").toString()
                    val login = document.get("login").toString()
                    val title = document.get("title").toString()
                    val imageUrl = document.get("image").toString()

                    val newItem = GoalHomeModel(title, imageUrl, name, login)
                    adapter.goalHomeList.add(newItem)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun navigation(){

        binding.profile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.addFragment)
        }

    }

}