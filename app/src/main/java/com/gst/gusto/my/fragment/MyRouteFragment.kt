package com.gst.gusto.my

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMyRouteBinding

class MyRouteFragment() : Fragment() {

    lateinit var binding: FragmentMyRouteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRouteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fl_my_route_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.popBackStack()

        navController.navigate(R.id.myRouteRoutesFragment)
    }
}