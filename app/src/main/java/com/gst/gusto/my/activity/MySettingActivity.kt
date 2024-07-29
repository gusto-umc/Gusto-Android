package com.gst.gusto.my.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.ActivityMySettingBinding
import com.gst.gusto.start.StartActivity
import com.gst.gusto.util.GustoApplication
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback

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
        setReviewButton()
    }

    override fun onPause() {
        setPublishData()
        super.onPause()
    }

    fun setReviewButton(){
        with(binding){
            val buttons = listOf(instaButton, calendarButton, listButton)

            buttons.forEach { button ->

                // selected 초기 세팅
                button.isSelected = button.id == GustoApplication.prefs.getReviewSharedPrefs()

                // button 클릭시
                button.setOnClickListener {
                    buttons.forEach { it.isSelected = false }
                    button.isSelected = true
                    GustoApplication.prefs.setReviewSharePrefs(button.id)
                }

            }
        }
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
            unregister.setOnClickListener {
                gustoViewModel.unregister { response ->
                    if(response==1) {
                        GustoApplication.prefs.setSharedPrefsBoolean("logout",true)
                        val social = GustoApplication.prefs.getSharedPrefsString("social")
                        if(social=="naver"){
                            startNaverDeleteToken()
                        } else if(social=="google") {
                            startGoogleDeletetoken()
                        } else if(social=="kakao") {
                            startKakaoDeleteToken()
                        }
                    } else {
                        Toast.makeText(this@MySettingActivity, "회원탈퇴 실패", Toast.LENGTH_SHORT).show()
                    }
                }


            }
            logout.setOnClickListener {
                GustoApplication.prefs.setSharedPrefsBoolean("logout",true)
                val social = GustoApplication.prefs.getSharedPrefsString("social")
                if(social=="naver"){
                    NaverIdLoginSDK.logout()
                } else if(social=="google") {
                    logoutGoogle()
                } else if(social=="kakao") {
                    logoutKakao()
                }
                gustoViewModel.logout { response ->
                    if(response==1) {
                        val intent = Intent(this@MySettingActivity, StartActivity::class.java)
                        startActivity(intent)
                        finish()

                        Toast.makeText(this@MySettingActivity, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MySettingActivity, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    }
                }
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
                        binding.jimSwitch.isChecked = value?.publishPin ?: false
                    })
                }
            }
        }
    }

    fun setPublishData() {
        with(binding) {
            val reviewSwitch = this.reviewSwitch.isChecked
            val pinSwitch = this.jimSwitch.isChecked
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

    fun startNaverDeleteToken(){
        NidOAuthLogin().callDeleteTokenApi(this, object : OAuthLoginCallback {
            override fun onSuccess() {
                //서버에서 토큰 삭제에 성공한 상태입니다.
                val intent = Intent(this@MySettingActivity, StartActivity::class.java)
                startActivity(intent)
                finish()
                //Toast.makeText(this@MySettingActivity, "네이버 아이디 토큰삭제 성공!", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Log.d("naver", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.d("naver", "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }
            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                onFailure(errorCode, message)
            }
        })
    }

    fun startKakaoDeleteToken() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.d("kakao", "error")
            }
            else {
                val intent = Intent(this@MySettingActivity, StartActivity::class.java)
                startActivity(intent)
                finish()
                //Toast.makeText(this@MySettingActivity, "카카오 아이디 토큰삭제 성공!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun startGoogleDeletetoken() {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("https://www.googleapis.com/auth/pubsub"))
            .requestEmail() // 이메일도 요청할 수 있다.
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        googleSignInClient.revokeAccess().addOnCompleteListener {
            val intent = Intent(this@MySettingActivity, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun logoutGoogle() {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("https://www.googleapis.com/auth/pubsub"))
            .requestEmail() // 이메일도 요청할 수 있다.
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        googleSignInClient.signOut().addOnCompleteListener {
        }
    }
    fun logoutKakao() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.d("kakao", "error")
            }
            else {
            }
        }
    }
}