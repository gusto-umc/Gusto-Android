package com.gst.gusto.my.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.ActivityMySettingBinding

class MySettingActivity : AppCompatActivity() {

    lateinit var binding: ActivityMySettingBinding
    private val gustoViewModel : GustoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySettingBinding.inflate(layoutInflater)

        buttonSetting()
        gustoViewModel.getTokens()
        getPublishData()
        setContentView(binding.root)
    }

    override fun onPause() {
        setPublishData()
        super.onPause()
    }

    fun buttonSetting() {
        binding.apply {
            btnBack.setOnClickListener{
                finish()
            }
            profileSetting.setOnClickListener {
                val intent = Intent(this@MySettingActivity, MyProfileEditActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun getPublishData() {
        gustoViewModel.myPublishGet { result, response ->
            if(result == 1) {
                if(response != null) {
                    Log.d("publish", response.toString())
                    gustoViewModel.myPublishData.observe(this, Observer { value ->
                        binding.reviewSwitch.isChecked = value?.publishReview ?: false
                        binding.pinSwitch.isChecked = value?.publishPin ?: false
                    })
                }
            }
        }
    }

    fun setPublishData() {
        with(binding) {
            val reviewSwitch = this.reviewSwitch.isChecked
            val pinSwitch = this.pinSwitch.isChecked
            gustoViewModel.myPublishSet(reviewSwitch,pinSwitch) {result, ->
            when(result) {
                1 -> {
                    Log.d("publishSet", result.toString())
                }
                else -> Toast.makeText(this@MySettingActivity, "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}