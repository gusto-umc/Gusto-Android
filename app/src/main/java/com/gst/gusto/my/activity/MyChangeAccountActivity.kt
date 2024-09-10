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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.ActivityMyChangeAccountBinding
import com.gst.gusto.databinding.ActivityMyProfileEditBinding
import com.gst.gusto.my.Age
import com.gst.gusto.my.Gender
import com.gst.gusto.my.fragment.MyProfileImageEditBottomSheet
import com.gst.gusto.my.viewmodel.MyProfileEditViewModel
import com.gst.gusto.my.viewmodel.MyProfileEditViewModelFactory
import com.gst.gusto.util.GustoApplication
import com.gst.gusto.util.util.Companion.convertContentToFile
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.util.util.Companion.toAbsolutePath

// MyProfileFragment 참고해서 디자인

class MyChangeAccountActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyChangeAccountBinding

    private val gustoViewModel : GustoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyChangeAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gustoViewModel.getTokens()

        gustoViewModel.getConnectedSocialList { result,response ->
            if(result==1) {
                if (response != null) {
                    if(response.NAVER) {
                        binding.lyNaver.visibility = View.VISIBLE
                    }
                    if(response.KAKAO) {
                        binding.lyNaver.visibility = View.VISIBLE
                    }
                    if(response.GOOGLE) {
                        binding.lyNaver.visibility = View.VISIBLE
                    }

                }
            } else {
                Log.d("test!!!!",response.toString())
            }
        }
    }

}


