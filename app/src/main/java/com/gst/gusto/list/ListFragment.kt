package com.gst.gusto.list

import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentListBinding

class ListFragment : Fragment() {

    lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fl_list_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.navigate(R.id.fragment_list_group)

        binding.btnGroup.setOnClickListener {
            binding.btnGroup.setBackgroundColor(Color.parseColor("#FEB520"))
            binding.ivGroup.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.tvGroup.setTextColor(Color.parseColor("#FFFFFF"))

            binding.btnRoute.setBackgroundColor(Color.parseColor("#F3F3F3"))
            binding.ivRoute.setColorFilter(Color.parseColor("#FFD704"))
            binding.tvRoute.setTextColor(Color.parseColor("#828282"))
            navController.navigate(R.id.fragment_list_group)
        }

        binding.btnRoute.setOnClickListener {
            binding.btnRoute.setBackgroundColor(Color.parseColor("#FEB520"))
            binding.ivRoute.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.tvRoute.setTextColor(Color.parseColor("#FFFFFF"))

            binding.btnGroup.setBackgroundColor(Color.parseColor("#F3F3F3"))
            binding.ivGroup.setColorFilter(Color.parseColor("#FFD704"))
            binding.tvGroup.setTextColor(Color.parseColor("#828282"))

            navController.navigate(R.id.fragment_list_route)
        }

        return binding.root

    }

}