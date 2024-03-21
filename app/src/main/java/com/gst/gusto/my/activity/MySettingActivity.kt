package com.gst.gusto.my.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.ActivityMySettingBinding

class MySettingActivity : AppCompatActivity() {

    lateinit var binding: ActivityMySettingBinding
    private val gustoViewModel : GustoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySettingBinding.inflate(layoutInflater)

        buttonSetting()
        gustoViewModel.getTokens(this)
        getPublishData()
        setContentView(binding.root)
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

}