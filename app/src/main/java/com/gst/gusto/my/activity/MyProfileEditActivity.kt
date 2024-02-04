package com.gst.gusto.my.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gst.gusto.R
import com.gst.gusto.databinding.ActivityMyProfileEditBinding

// MyProfileFragment 참고해서 디자인

class MyProfileEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyProfileEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileEditBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}