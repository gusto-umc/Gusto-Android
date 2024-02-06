package com.gst.gusto.start

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.gst.gusto.api.LoginViewModel
import com.gst.gusto.R
import com.gst.gusto.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    lateinit var binding: ActivityStartBinding
    lateinit var navController : NavController
    private lateinit var handler: Handler
    private val timerInterval: Long = 15 * 60 * 1000 // 15분을 밀리초로 계산
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)

        handler = Handler(Looper.getMainLooper())

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.start_fl_container) as NavHostFragment
        navController = navHostFragment.navController


        setContentView(binding.root)
    }
    override fun finish() {
        val viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val sharedPref = getSharedPreferences("token_pref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("accessToken", viewModel.getAccessToken())
        editor.putString("refreshToken", viewModel.getRefreshToken())
        Log.d("tokens", "${viewModel.getRefreshToken()}, ${viewModel.getAccessToken()}")
        editor.apply()
        stopTimer()

        super.finish()
    }

    fun startTimer() {
        handler.postDelayed(timerRunnable, timerInterval)
    }
    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
    }
    private val timerRunnable = object : Runnable {
        override fun run() {
            // 타이머가 만료되면 맨 처음 프래그먼트로 이동
            moveToFirstFragment()
            stopTimer()
        }
    }

    private fun moveToFirstFragment() {
        Toast.makeText(applicationContext, "회원가입 시간(15분)이 만료되었습니다", Toast.LENGTH_SHORT).show()
        navController.navigate(R.id.fragment_login)
    }



}