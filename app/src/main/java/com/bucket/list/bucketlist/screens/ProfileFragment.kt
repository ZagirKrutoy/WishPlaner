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
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.adapters.CompletGoalAdapter
import com.bucket.list.bucketlist.adapters.UserGoalAdapter
import com.bucket.list.bucketlist.databinding.FragmentProfileBinding
import com.bucket.list.bucketlist.models.AddModel
import com.bucket.list.bucketlist.models.CompleteGoalModel
import com.bucket.list.bucketlist.models.OnItemClickListener
import com.bucket.list.bucketlist.models.UserGoalModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class ProfileFragment : Fragment(), OnItemClickListener {
    lateinit var binding : FragmentProfileBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var completeAdapter : CompletGoalAdapter
    lateinit var userAdapter : UserGoalAdapter
    lateinit var defaulRv : RecyclerView
    lateinit var completeRv : RecyclerView

    private val db = Firebase.firestore
    private var login = ""
    private var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
        login = sharedPreferences.getString("login", "").toString()
        Log.w("TAG", "Login : $login")
        name = sharedPreferences.getString("name", "").toString()
        initial()
        getUserData()
        getCompleteData()

        navigation()
    }

    private fun getUserData() {
        db.collection("goals")
            .whereEqualTo("login", "@$login")
            .whereEqualTo("isComplete", false)
            .get()
            .addOnSuccessListener { result ->
                userAdapter.userGoalList.clear()
                for (document in result) {
                    val login = document.getString("login").orEmpty()
                    val title = document.getString("title").orEmpty()

                    val newItem = UserGoalModel(title, login)
                    userAdapter.userGoalList.add(newItem)
                }
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun getCompleteData() {
        db.collection("goals")
            .whereEqualTo("isComplete", true)
            .whereEqualTo("login", "@$login")
            .get()
            .addOnSuccessListener { result ->
                completeAdapter.completeGoalList.clear()
                for (document in result) {
                    val login = document.getString("login").orEmpty()
                    val title = document.getString("title").orEmpty()

                    val newItem = CompleteGoalModel(title, login)
                    completeAdapter.completeGoalList.add(newItem)
                }
                completeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun initial(){
        completeAdapter = CompletGoalAdapter(this)
        userAdapter = UserGoalAdapter { userGoalModel ->
            db.collection("goals")
                .whereEqualTo("login", userGoalModel.login)
                .whereEqualTo("title", userGoalModel.title)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result){
                        val documentId = document.id
                        db.collection("goals")
                            .document(documentId)
                            .update(
                                mapOf(
                                    "isComplete" to true
                                )
                            )
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Выполнено", Toast.LENGTH_SHORT)
                                    .show()
                                getCompleteData()
                                getUserData()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
        }
        defaulRv = binding.defaultRv
        completeRv = binding.rv

        defaulRv.adapter = userAdapter
        completeRv.adapter = completeAdapter

        defaulRv.layoutManager = LinearLayoutManager(requireContext())
        completeRv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun navigation(){

        binding.home.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.addFragment)
        }

    }

    override fun OnClickListener(complete: CompleteGoalModel) {
        val bundle = Bundle().apply {
            putString("title", complete.title)
            putString("login", complete.login)
            putString("name", name)
        }
        findNavController().navigate(R.id.detalFragment, bundle)
    }

}