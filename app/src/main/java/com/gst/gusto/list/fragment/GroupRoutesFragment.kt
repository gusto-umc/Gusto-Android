package com.gst.gusto.list.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMRoutesBinding

class GroupRoutesFragment(val num: Int) : Fragment() {

    lateinit var binding: FragmentListGroupMRoutesBinding
    lateinit var navController : NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRoutesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fl_routes_container) as NavHostFragment
        navController = navHostFragment.navController

        if(num==0)navController.navigate(R.id.fragment_group_m_route_routes)
        else navController.navigate(R.id.fragment_group_m_route_stores)
    }
    public fun getCon() : NavController{
        return navController
    }




}