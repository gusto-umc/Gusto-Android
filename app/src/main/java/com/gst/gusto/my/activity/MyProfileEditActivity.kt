package com.gst.gusto.my.activity

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.gst.gusto.R
import com.gst.gusto.databinding.ActivityMyProfileEditBinding

// MyProfileFragment 참고해서 디자인

class MyProfileEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyProfileEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileEditBinding.inflate(layoutInflater)

        var test = true

        binding.apply {
            // 테스트용
            ivProfileImage.setOnClickListener {
                test = !test
                checkNickName(test)
            }
            btnBack.setOnClickListener{
                finish()
            }
        }

        setContentView(binding.root)
    }

    fun checkNickName(check: Boolean){
        binding.apply {
            if(check){
                checkBtn.visibility = View.INVISIBLE
                checkIcon.visibility = View.VISIBLE
                nicknameCheck.visibility = View.VISIBLE
                nicknameCheck.text = "사용 가능한 닉네임입니다."
                nicknameCheck.setTextColor(Color.parseColor("#F27781"))
                nicknameCheck.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A6A6A6"))
            } else {
                checkBtn.visibility = View.VISIBLE
                checkIcon.visibility = View.INVISIBLE
                nicknameCheck.visibility = View.VISIBLE
                nicknameCheck.text = "이미 사용중인 닉네임입니다."
                nicknameCheck.setTextColor(Color.parseColor("#FD6907"))
                nicknameCheck.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FD6907"))

            }
        }
    }
}