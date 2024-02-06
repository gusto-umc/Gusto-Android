package com.gst.gusto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gst.gusto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fl_container) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)

        setContentView(binding.root)
    }

    fun getCon() : NavController{
        return navController
    }

    override fun onBackPressed() {
        // 현재 NavController의 백 스택을 확인하고, 이전 destination으로 이동합니다.
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }

}