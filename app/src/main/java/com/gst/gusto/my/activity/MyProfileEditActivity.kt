package com.gst.gusto.my.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gst.gusto.R
import com.gst.gusto.databinding.ActivityMyProfileEditBinding
import com.gst.gusto.my.Age
import com.gst.gusto.my.Gender
import com.gst.gusto.my.fragment.MyProfileImageEditBottomSheet
import com.gst.gusto.my.viewmodel.MyProfileEditViewModel
import com.gst.gusto.my.viewmodel.MyProfileEditViewModelFactory
import com.gst.gusto.util.util.Companion.convertContentToFile
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.util.util.Companion.toAbsolutePath

// MyProfileFragment 참고해서 디자인

class MyProfileEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyProfileEditBinding

    private val viewModel: MyProfileEditViewModel by viewModels{ MyProfileEditViewModelFactory() }

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var albumLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { drawImage(binding.profileImageView, it) }
        }

        albumLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let { drawImage(binding.profileImageView, uri) }
            }
        }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startAlbumLauncher()
                } else {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val packageName = this.packageName
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }

        with(binding) {
            btnBack.setOnClickListener{
                finish()
            }
            saveBtn.setOnClickListener {
                viewModel.setProfile()
            }

            viewModel.setProfileData.observe(this@MyProfileEditActivity){
                if(it){
                    setResult(RESULT_OK)
                    finish()
                }
            }

            changeProfileImageBtn.setOnClickListener {
                MyProfileImageEditBottomSheet(
                    onBasicProfileClickListener = {
                        profileImageView.setImageResource(R.drawable.gst_dummypic)
                        viewModel.setDefaultProfileImg()
                    },
                    onProfileClickListener = {
                        if (isPhotoPickerAvailable()) {
                            this@MyProfileEditActivity.pickMedia.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else {
                            checkPermission()
                        }
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

    private fun drawImage(view: ImageView, imgUrl: Uri) {
        Glide.with(this)
            .load(imgUrl)
            .into(view)

        // val path = this.toAbsolutePath(imgUrl)
        val path = convertContentToFile(this@MyProfileEditActivity, imgUrl).absolutePath.toString()
        viewModel.setProfileImg(path)
    }

    private fun checkPermission() {
        val permissionReadExternal = android.Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionReadExternalGranted = ContextCompat.checkSelfPermission(
            this,
            permissionReadExternal
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionReadExternalGranted) {
            startAlbumLauncher()
        } else {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun startAlbumLauncher() {
        val albumIntent = Intent(Intent.ACTION_PICK)
        albumIntent.type = "image/*"
        albumLauncher.launch(albumIntent)
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> true
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> SdkExtensions.getExtensionVersion(
                Build.VERSION_CODES.R
            ) >= 2

            else -> false
        }
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
                setupAgePopupMenu(it?.age.toString())

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

    private fun setupAgePopupMenu(currentAge: String){
        val age = Age.valueOf(currentAge)

        with(binding){
            ageBtn.setOnClickListener {
                val contextThemeWrapper = ContextThemeWrapper(this@MyProfileEditActivity, R.style.MyProfileEditPopupMenu)
                val popupMenu = PopupMenu(contextThemeWrapper, it)
                popupMenu.menuInflater.inflate(R.menu.profile_edit_age_menu, popupMenu.menu)

                when(age){
                    Age.TEEN -> popupMenu.menu.findItem(R.id.menu_teen).isEnabled = false
                    Age.TWENTIES -> popupMenu.menu.findItem(R.id.menu_twenties).isEnabled = false
                    Age.THIRTIES -> popupMenu.menu.findItem(R.id.menu_thirties).isEnabled = false
                    Age.FOURTIES -> popupMenu.menu.findItem(R.id.menu_fourties).isEnabled = false
                    Age.FIFTIES -> popupMenu.menu.findItem(R.id.menu_fifties).isEnabled = false
                    Age.OLDER -> popupMenu.menu.findItem(R.id.menu_older).isEnabled = false
                    Age.NONE -> popupMenu.menu.findItem(R.id.menu_unselected).isEnabled = false
                }

                popupMenu.setOnMenuItemClickListener { item ->
                    ageBtn.text = item.title.toString()
                    viewModel.setAge(item.title.toString())
                    true
                }

                popupMenu.show()

            }
        }
    }

    private fun setupGenderPopupMenu(currentGender: String) {
        val gender = Gender.valueOf(currentGender)

        with(binding){
            genderBtn.setOnClickListener {
                val contextThemeWrapper = ContextThemeWrapper(this@MyProfileEditActivity, R.style.MyProfileEditPopupMenu)
                val popupMenu = PopupMenu(contextThemeWrapper, it)
                popupMenu.menuInflater.inflate(R.menu.profile_edit_gender_menu, popupMenu.menu)

                when (gender) {
                    Gender.FEMALE -> popupMenu.menu.findItem(R.id.menu_female).isEnabled = false
                    Gender.MALE -> popupMenu.menu.findItem(R.id.menu_male).isEnabled = false
                    Gender.NONE -> popupMenu.menu.findItem(R.id.menu_unselected).isEnabled = false
                }

                popupMenu.setOnMenuItemClickListener { item ->
                    genderBtn.text = item.title.toString()
                    viewModel.setGender(item.title.toString())
                    true
                }
                popupMenu.show()
            }
        }
    }

    private fun checkNickName(){

        viewModel.checkNickNameData.observe(this@MyProfileEditActivity){
            with(binding){
                if(it){
                    viewModel.setNickname(nicknameEditText.text.toString())
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


