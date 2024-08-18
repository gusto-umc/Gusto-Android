package com.gst.gusto.my.activity

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import com.gst.gusto.R
import com.gst.gusto.databinding.ActivityMyProfileEditBinding
import com.gst.gusto.my.Age
import com.gst.gusto.my.Gender
import com.gst.gusto.my.fragment.MyProfileImageEditBottomSheet
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
        setContentView(binding.root)

        with(binding) {
            btnBack.setOnClickListener{
                finish()
            }
            saveBtn.setOnClickListener {
                finish()
            }
            changeProfileImageBtn.setOnClickListener {
                MyProfileImageEditBottomSheet(
                    onBasicProfileClickListener = {
                        profileImageView.setImageResource(R.drawable.gst_dummypic)
                    },
                    onProfileClickListener = {

                    }
                ).show(supportFragmentManager, "MyProfileImageEditBottomSheet")
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
    }

    private fun getProfileInfo() {
        with(binding){

            viewModel.myProfileData.observe(this@MyProfileEditActivity){
                nicknameEditText.setText(it?.nickname)
                setImage(profileImageView, it?.profileImg, root.context)

                val ageGroup = it?.age?.let { age -> Age.valueOf(age) }

                when (ageGroup) {
                    Age.TEEN -> ageBtn.text = Age.TEEN.displayName
                    Age.TWENTIES -> ageBtn.text = Age.TWENTIES.displayName
                    Age.THIRTIES -> ageBtn.text = Age.THIRTIES.displayName
                    Age.FOURTIES -> ageBtn.text = Age.FOURTIES.displayName
                    Age.FIFTIES -> ageBtn.text = Age.FIFTIES.displayName
                    Age.OLDER -> ageBtn.text = Age.OLDER.displayName
                    Age.NONE -> ageBtn.text = Age.NONE.displayName
                    null -> ageBtn.text = "알 수 없음"
                }

                val gender = it?.gender?.let { gender -> Gender.valueOf(gender) }

                when (gender) {
                    Gender.FEMALE -> genderBtn.text = Gender.FEMALE.displayName
                    Gender.MALE -> genderBtn.text = Gender.MALE.displayName
                    Gender.NONE -> genderBtn.text = Gender.NONE.displayName
                    null -> genderBtn.text = "알 수 없음"
                }
                setupGenderPopupMenu(it?.gender.toString())
            }
        }
    }

    private fun setupGenderPopupMenu(currentGender: String) {
        val gender = Gender.valueOf(currentGender)

        binding.genderBtn.setOnClickListener { btn ->
            val contextThemeWrapper = ContextThemeWrapper(this, R.style.MyProfileEditPopupMenu)
            val popupMenu = PopupMenu(contextThemeWrapper, btn)
            popupMenu.menuInflater.inflate(R.menu.profile_edit_gender_menu, popupMenu.menu)

            when (gender) {
                Gender.FEMALE -> popupMenu.menu.findItem(R.id.menu_female).isEnabled = false
                Gender.MALE -> popupMenu.menu.findItem(R.id.menu_male).isEnabled = false
                Gender.NONE -> popupMenu.menu.findItem(R.id.menu_unselected).isEnabled = false
            }

            popupMenu.setOnMenuItemClickListener { item ->
                binding.genderBtn.text = item.title.toString()
                viewModel.setGender(item.title.toString())
                true
            }
            popupMenu.show()
        }
    }
    
    private fun checkNickName(){

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


