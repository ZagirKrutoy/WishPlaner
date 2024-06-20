package com.bucket.list.bucketlist.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.adapters.AddAdapter
import com.bucket.list.bucketlist.adapters.GoalHomeAdapter
import com.bucket.list.bucketlist.databinding.FragmentAddBinding
import com.bucket.list.bucketlist.models.AddModel
import com.bucket.list.bucketlist.models.GoalHomeModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale
import kotlin.math.log


class AddFragment : Fragment() {
    lateinit var binding : FragmentAddBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter : AddAdapter
    lateinit var rv : RecyclerView
    private val db = Firebase.firestore
    private val loadedList: MutableList<AddModel> = mutableListOf()

    private var login = ""
    private var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
        login = sharedPreferences.getString("login", "").toString()
        name = sharedPreferences.getString("name", "").toString()

        binding.addGoal.setOnClickListener {
            myGoal()
        }

        navigation()

        getData()

        rv = binding.rv
        adapter = AddAdapter { addModel ->
            val goal = hashMapOf(
                "title" to addModel.title,
                "login" to "@$login",
                "name" to name,
                "image" to ""
            )

            db.collection("goals")
                .add(goal)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(requireContext(), "Добавлено", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
        }
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        binding.search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.text.text = s.toString()
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun filter(query: String) {
        val filteredList = if (query.isBlank()) {
            mutableListOf<AddModel>()
        } else {
            loadedList.filter {
                it.title.toLowerCase(Locale.getDefault())
                    .contains(query.toLowerCase(Locale.getDefault()))
            }.toMutableList()
        }

        adapter.addList = filteredList
        adapter.notifyDataSetChanged()
    }

    private fun myGoal(){
        val goal = hashMapOf(
            "title" to binding.text.text.toString(),
            "login" to "@$login",
            "name" to name,
            "isComplete" to false
        )

        db.collection("goals")
            .add(goal)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireContext(), "Добавлено", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

    private fun getData() {
        db.collection("goals")
            .get()
            .addOnSuccessListener { result ->
                loadedList.clear()
                for (document in result) {
                    val login = document.get("login").toString()
                    val title = document.get("title").toString()

                    val newItem = AddModel(title, login)
                    loadedList.add(newItem)
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

        binding.home.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

    }

}