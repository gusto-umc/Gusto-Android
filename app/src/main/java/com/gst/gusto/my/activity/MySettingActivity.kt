package com.gst.gusto.my.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gst.gusto.R
import com.gst.gusto.databinding.ActivityMySettingBinding
import com.gst.gusto.start.StartActivity

class MySettingActivity : AppCompatActivity() {

    lateinit var binding: ActivityMySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySettingBinding.inflate(layoutInflater)

        binding.apply {
            btnBack.setOnClickListener{
                finish()
            }
            profileSetting.setOnClickListener {
                val intent = Intent(this@MySettingActivity, MyProfileEditActivity::class.java)
                startActivity(intent)
            }
        }
        setContentView(binding.root)
    }
}