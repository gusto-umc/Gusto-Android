package com.gst.gusto.my.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMyProfileBinding

class MyProfileFragment : Fragment() {

    lateinit var binding: FragmentMyProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_myProfile_to_myFragment)
        }

        return binding.root

    }
}