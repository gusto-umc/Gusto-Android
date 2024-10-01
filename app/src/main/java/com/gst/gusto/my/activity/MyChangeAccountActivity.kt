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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.gst.gusto.BuildConfig
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.AccessTokenResponse
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.LoginApi
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
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// MyProfileFragment 참고해서 디자인

class MyChangeAccountActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyChangeAccountBinding

    private val gustoViewModel : GustoViewModel by viewModels()

    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }

    private var remove = false

    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)

            Log.d("GOOGLE_Login","${account.id}")
            Log.d("GOOGLE_Login","${account.photoUrl}")
            Log.d("GOOGLE_Login","${account.idToken}")
            Log.d("GOOGLE_Login","${account.serverAuthCode}")

            getAccessToken("${account.id}","${account.photoUrl}",account.serverAuthCode)
        } catch (e: ApiException) {
            Log.e("GOOGLE_Login", e.stackTraceToString())
        }
    }
    private fun getAccessToken(id: String, photoUrl: String,serverAuthCode: String?) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://oauth2.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginApi::class.java)

        val call = service.getAccessToken("authorization_code", serverAuthCode, BuildConfig.GOOGLE_CLINET_ID, BuildConfig.GOOGLE_SECRET, BuildConfig.GOOGLE_REDIRECT)
        call.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
                if (response.isSuccessful) {
                    val accessToken = response.body()?.accessToken
                    if (accessToken != null) {
                        successGoogleLogin(id,photoUrl,accessToken)
                    }
                    Log.d("GOOGLE_LOGIN_TOKEN", "Access Token: $accessToken")
                } else {
                    Log.e("GOOGLE_LOGIN_TOKEN", "Access Token request failed ${response}")
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                Log.e("GOOGLE_LOGIN_TOKEN", "Access Token request error", t)
            }
        })
    }

    var provider =""
    var providerId =""
    var accessToken=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyChangeAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gustoViewModel.getTokens()

        gustoViewModel.getConnectedSocialList { result,response ->
            if(result==1) {
                Log.d("viewmodel getConnected Social List",response.toString())
                if (response != null) {
                    if(response.NAVER) onNaver()
                    else offNaver()
                    if(response.KAKAO) onKakao()
                    else offKakao()
                    if(response.GOOGLE) onGoogle()
                    else offGoogle()
                }
            } else {
                Log.d("test!!!!",response.toString())
            }
        }

        binding.btnPlusNaver.setOnClickListener {
            remove = false
            startNaverLogin()
        }
        binding.btnPlusKakao.setOnClickListener {
            remove = false
            startKakaoLogin()
        }
        binding.btnPlusGoogle.setOnClickListener {
            remove = false
            startGoogleLogin()
        }
        binding.btnXNaver.setOnClickListener {
            remove = true
            startNaverLogin()
        }
        binding.btnXKakao.setOnClickListener {
            remove = true
            startKakaoLogin()
        }
        binding.btnXGoogle.setOnClickListener {
            remove = true
            startGoogleLogin()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    fun onNaver() {
        binding.tvTxtNaver.text = "연동이 완료된 플랫폼입니다"
        binding.tvConnectNaver.visibility = View.GONE
        binding.btnXNaver.visibility = View.VISIBLE
        binding.btnPlusNaver.visibility = View.GONE
    }
    fun offNaver() {
        binding.tvConnectNaver.visibility = View.VISIBLE
        binding.btnXNaver.visibility = View.GONE
        binding.btnPlusNaver.visibility = View.VISIBLE
    }
    fun onKakao() {
        binding.tvTxtKakao.text = "연동이 완료된 플랫폼입니다"
        binding.tvConnectKakao.visibility = View.GONE
        binding.btnXKakao.visibility = View.VISIBLE
        binding.btnPlusKakao.visibility = View.GONE
    }
    fun offKakao() {
        binding.tvConnectKakao.visibility = View.VISIBLE
        binding.btnXKakao.visibility = View.GONE
        binding.btnPlusKakao.visibility = View.VISIBLE
    }
    fun onGoogle() {
        binding.tvTxtGoogle.text = "연동이 완료된 플랫폼입니다"
        binding.tvConnectGoogle.visibility = View.GONE
        binding.btnXGoogle.visibility = View.VISIBLE
        binding.btnPlusGoogle.visibility = View.GONE
    }
    fun offGoogle() {
        binding.tvConnectGoogle.visibility = View.VISIBLE
        binding.btnXGoogle.visibility = View.GONE
        binding.btnPlusGoogle.visibility = View.VISIBLE
    }
    private fun startNaverLogin(){
        var naverToken :String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                Log.d("hellogogogo",response.toString())
                val tmpId = response.profile?.id

                provider = tmpId ?: ""
                providerId = "NAVER"

                if(remove)
                    gustoViewModel.unConnectSocial(providerId, provider, accessToken) { resultCode ->
                        when (resultCode) {
                            1 -> {
                                Toast.makeText(this@MyChangeAccountActivity, "네이버 연동 해제 성공", Toast.LENGTH_SHORT).show()
                                offNaver()
                            }
                            else -> {
                                Toast.makeText(this@MyChangeAccountActivity, "네이버 연동 해제 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                else
                    gustoViewModel.addConnectSocial(providerId, provider, accessToken) { resultCode ->
                        when (resultCode) {
                            1 -> {
                                Toast.makeText(this@MyChangeAccountActivity, "네이버 연동 성공", Toast.LENGTH_SHORT).show()
                                onNaver()
                            }
                            else -> {
                                Toast.makeText(this@MyChangeAccountActivity, "네이버 연동 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        /** OAuthLoginCallback을 authenticate() 메서드 호출 시 파라미터로 전달하거나 NidOAuthLoginButton 객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다. */
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                naverToken = NaverIdLoginSDK.getAccessToken()
                accessToken = naverToken.toString()
                /*
                LoginViewModel.setRefreshToken(NaverIdLoginSDK.getRefreshToken().toString())
                var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
                var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
                var naverTokenType = NaverIdLoginSDK.getTokenType()
                var naverState = NaverIdLoginSDK.getState().toString()*/

                //로그인 유저 정보 가져오기
                NidOAuthLogin().callProfileApi(profileCallback)
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                //Toast.makeText(this@MainActivity, "errorCode: ${errorCode}\n" +"errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
    }

    private fun startKakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KAKAO", "카카오 연동 실패", error)
            } else if (token != null) {
                successKakaoLogin(token.accessToken)
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e("KAKAO", "카카오톡 연동 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)



                } else if (token != null) {
                    successKakaoLogin(token.accessToken)
                    Log.i("KAKAO", "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }
    private fun successKakaoLogin(accessToken : String) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
            } else if (user != null) {
                Log.e("KAKAO", "사용자 정보 요청 성공 : $user")
                providerId = user.id.toString()
                provider = "KAKAO"
                this.accessToken = accessToken
                if(remove)
                    gustoViewModel.unConnectSocial(provider, providerId, this.accessToken) { resultCode ->
                        when (resultCode) {
                            1 -> {
                                Toast.makeText(this@MyChangeAccountActivity, "카카오 연동 해제 성공", Toast.LENGTH_SHORT).show()
                                offKakao()
                            }
                            else -> {
                                Toast.makeText(this@MyChangeAccountActivity, "카카오 연동 해제 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                else
                    gustoViewModel.addConnectSocial(provider, providerId, this.accessToken) { resultCode ->
                        when (resultCode) {
                            1 -> {
                                Toast.makeText(this@MyChangeAccountActivity, "카카오 연동 성공", Toast.LENGTH_SHORT).show()
                                onKakao()
                            }
                            else -> {
                                Toast.makeText(this@MyChangeAccountActivity, "카카오 연동 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }
    }


    private fun startGoogleLogin() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)

    }
    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("https://www.googleapis.com/auth/pubsub"))
            .requestIdToken(BuildConfig.GOOGLE_CLINET_ID)
            .requestServerAuthCode(BuildConfig.GOOGLE_CLINET_ID)
            .requestEmail() // 이메일도 요청할 수 있다.
            .build()

        return GoogleSignIn.getClient(this, googleSignInOption)
    }

    private fun successGoogleLogin(id: String, photoUrl: String,socialAccessToken : String) {
        providerId = id
        provider = "GOOGLE"
        accessToken = socialAccessToken

        if(remove)
            gustoViewModel.unConnectSocial(providerId, provider, this.accessToken) { resultCode ->
                when (resultCode) {
                    1 -> {
                        Toast.makeText(this@MyChangeAccountActivity, "구글 연동 해제 성공", Toast.LENGTH_SHORT).show()
                        offGoogle()
                    }
                    else -> {
                        Toast.makeText(this@MyChangeAccountActivity, "구글 연동 해제 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        else
            gustoViewModel.addConnectSocial(provider, providerId, this.accessToken) { resultCode ->
                when (resultCode) {
                    1 -> {
                        Toast.makeText(this@MyChangeAccountActivity, "구글 연동 성공", Toast.LENGTH_SHORT).show()
                        onGoogle()
                    }
                    else -> {
                        Toast.makeText(this@MyChangeAccountActivity, "구글 연동 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}


