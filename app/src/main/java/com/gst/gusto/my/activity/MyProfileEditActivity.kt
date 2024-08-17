package com.gst.gusto.my.activity

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.gst.gusto.R
import com.gst.gusto.databinding.ActivityMyProfileEditBinding
import com.gst.gusto.my.viewmodel.MyProfileEditViewModel
import com.gst.gusto.my.viewmodel.MyProfileEditViewModelFactory
import com.gst.gusto.util.util.Companion.setImage

// MyProfileFragment 참고해서 디자인

class MyProfileEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyProfileEditBinding

    private val viewModel: MyProfileEditViewModel by viewModels{ MyProfileEditViewModelFactory() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileEditBinding.inflate(layoutInflater)

        with(binding) {
            btnBack.setOnClickListener{
                finish()
            }
            saveBtn.setOnClickListener {
                finish()
            }
            checkBtn.setOnClickListener {
                viewModel.getCheckNickname(nicknameEditText.text.toString())
            }
            checkIcon.setOnClickListener {
                viewModel.getCheckNickname(nicknameEditText.text.toString())
            }
        }

        getProfileInfo()
        checkNickName()

        setContentView(binding.root)
    }

    private fun getProfileInfo() {
        with(binding){

            viewModel.myProfileData.observe(this@MyProfileEditActivity){
                nicknameEditText.setText(it?.nickname)
                setImage(profileImageView, it?.profileImg, root.context)

                when(it?.age) {
                    "TEEN" -> ageBtn.text = "10대"
                    "TWENTIES" -> ageBtn.text = "20대"
                    "THIRTIES"-> ageBtn.text = "30대"
                    "FOURTIES"-> ageBtn.text = "40대"
                    "FIFTIES"-> ageBtn.text = "50대"
                    "OLDER"-> ageBtn.text = "60대"
                    "NONE"-> ageBtn.text = "선택하지 않음"
                }

                when(it?.gender) {
                    "FEMALE" -> genderBtn.text = "여자"
                    "MALE" -> genderBtn.text = "남자"
                    "NONE" -> genderBtn.text = "선택하지 않음"
                }
            }
        }
    }

    fun checkNickName(){

        viewModel.checkNickNameData.observe(this@MyProfileEditActivity){
            with(binding){
                if(it){
                    checkBtn.visibility = View.GONE
                    checkIcon.visibility = View.VISIBLE
                    nicknameCheck.visibility = View.VISIBLE
                    nicknameCheck.text = "사용 가능한 닉네임입니다."
                    saveBtn.isClickable = true
                    saveBtn.setTextColor(Color.parseColor("#F27781"))
                    nicknameCheck.setTextColor(Color.parseColor("#F27781"))
                    nicknameCheck.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A6A6A6"))
                } else {
                    checkBtn.visibility = View.VISIBLE
                    checkIcon.visibility = View.GONE
                    nicknameCheck.visibility = View.VISIBLE
                    saveBtn.isClickable = false
                    saveBtn.setTextColor(Color.parseColor("#A6A6A6"))
                    nicknameCheck.text = "이미 사용중인 닉네임입니다."
                    nicknameCheck.setTextColor(Color.parseColor("#FD6907"))
                    nicknameCheck.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FD6907"))

                }
            }
        }
    }
}