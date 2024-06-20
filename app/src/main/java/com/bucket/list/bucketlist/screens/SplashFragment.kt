package com.bucket.list.bucketlist.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
    lateinit var binding : FragmentSplashBinding
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)

        binding.go.setOnClickListener {
            navigation()
        }
    }

    private fun navigation(){
        val TY = sharedPreferences.getInt("TY", -9)

        if (TY > 0){
            findNavController().navigate(R.id.homeFragment)
        }else{
            findNavController().navigate(R.id.registrationFragment)
        }
    }

}