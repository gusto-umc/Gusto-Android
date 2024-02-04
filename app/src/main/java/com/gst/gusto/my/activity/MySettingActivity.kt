package com.gst.gusto.my.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gst.gusto.R
import com.gst.gusto.databinding.ActivityMySettingBinding

class MySettingActivity : AppCompatActivity() {

    lateinit var binding: ActivityMySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySettingBinding.inflate(layoutInflater)

        binding.apply {
            btnBack.setOnClickListener{
                finish()
            }
        }
        setContentView(binding.root)
    }
}