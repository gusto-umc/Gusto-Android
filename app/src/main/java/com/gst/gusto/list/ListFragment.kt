package com.gst.gusto.list

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentListMainBinding


class ListFragment : Fragment() {

    lateinit var binding: FragmentListMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListMainBinding.inflate(inflater, container, false)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fl_list_container) as NavHostFragment
        val navController = navHostFragment.navController



        navController.navigate(R.id.fragment_list_group)

        val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#FEB520"))
        val colorStateOffList = ColorStateList.valueOf(Color.parseColor("#F3F3F3"))
        binding.btnGroup.setOnClickListener {
            ViewCompat.setBackgroundTintList(binding.btnGroup, colorStateOnList)
            binding.ivGroup.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.tvGroup.setTextColor(Color.parseColor("#FFFFFF"))

            ViewCompat.setBackgroundTintList(binding.btnRoute, colorStateOffList)
            binding.ivRoute.setColorFilter(Color.parseColor("#FFD704"))
            binding.tvRoute.setTextColor(Color.parseColor("#828282"))
            navController.navigate(R.id.fragment_list_group)
        }

        binding.btnRoute.setOnClickListener {
            ViewCompat.setBackgroundTintList(binding.btnRoute, colorStateOnList)
            binding.ivRoute.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.tvRoute.setTextColor(Color.parseColor("#FFFFFF"))

            ViewCompat.setBackgroundTintList(binding.btnGroup, colorStateOffList)
            binding.ivGroup.setColorFilter(Color.parseColor("#FFD704"))
            binding.tvGroup.setTextColor(Color.parseColor("#828282"))

            navController.navigate(R.id.fragment_list_route)
        }

        return binding.root

    }

}