package com.gst.gusto.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.OnCompleteListener
import com.gst.gusto.BuildConfig
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.AccessTokenResponse
import com.gst.gusto.api.LoginApi
import com.gst.gusto.api.LoginViewModel
import com.gst.gusto.databinding.StartFragmentLoginBinding
import com.gst.gusto.util.GustoApplication
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
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


class LoginFragment: Fragment() {

    lateinit var binding: StartFragmentLoginBinding
    private val LoginViewModel : LoginViewModel by activityViewModels()
    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentLoginBinding.inflate(inflater, container, false)

        val naverClientId = BuildConfig.NAVER_CLIENT_ID
        val naverClientSecret = BuildConfig.NAVER_CLIENT_SECRET
        val naverClientName = "네이버아이디 로그인"
        NaverIdLoginSDK.initialize(requireContext(), naverClientId, naverClientSecret , naverClientName)

        KakaoSdk.init(requireContext(), BuildConfig.KAKAO_NATIVE_APP_KEY)

        binding.btnNaver.setOnClickListener {
            GustoApplication.prefs.setSharedPrefsString("social","naver")
            startNaverLogin()
        }
        binding.btnKakao.setOnClickListener {
            GustoApplication.prefs.setSharedPrefsString("social","kakao")
            startKakaoLogin()
        }
        binding.btnGoogle.setOnClickListener {
            GustoApplication.prefs.setSharedPrefsString("social","google")
            startGoogleLogin()
        }
        binding.btnNoLogin.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        MobileAds.initialize(requireContext())
        val adLoader = AdLoader.Builder(requireContext(),resources.getString(R.string.admob_native))
            .forNativeAd { nativeAd ->
                // Handle the native ad loaded callback
                val styles = NativeTemplateStyle.Builder()
                    .build()
                val template = binding.nativeAdTemplate
                template.setStyles(styles)
                template.setNativeAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    Log.e("AdLoader", "Failed to load ad: ${adError}")
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        binding.bannerAd.loadAd(AdRequest.Builder().build())

        return binding.root

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

        return GoogleSignIn.getClient(requireActivity(), googleSignInOption)
    }

    private fun successGoogleLogin(id: String, photoUrl: String,socialAccessToken : String) {
        LoginViewModel.providerId = id
        LoginViewModel.profileUrl = photoUrl
        LoginViewModel.provider = "GOOGLE"
        LoginViewModel.socialAccessToken = socialAccessToken

        LoginViewModel.login { resultCode ->
            when (resultCode) {
                1 -> {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                2 -> {
                    findNavController().navigate(R.id.action_loginFragment_to_nameFragment)
                }
                else -> {
                    Toast.makeText(requireContext(), "서버와의 통신 불안정!!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun startKakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KAKAO", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                successKakaoLogin(token.accessToken)
                Log.i("KAKAO", "카카오계정으로 로그인 성공 ${token.accessToken}")
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.e("KAKAO", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)



                } else if (token != null) {
                    successKakaoLogin(token.accessToken)
                    Log.i("KAKAO", "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
        }
    }
    private fun successKakaoLogin(accessToken : String) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
            } else if (user != null) {
                Log.e("KAKAO", "사용자 정보 요청 성공 : $user")
                LoginViewModel.providerId = user.id.toString()
                LoginViewModel.provider = "KAKAO"
                LoginViewModel.profileUrl = user.kakaoAccount?.profile?.profileImageUrl
                LoginViewModel.socialAccessToken = accessToken
                LoginViewModel.login { resultCode ->
                    when (resultCode) {
                        1 -> {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        2 -> {
                            findNavController().navigate(R.id.action_loginFragment_to_nameFragment)
                        }
                        else -> {
                            Toast.makeText(requireContext(), "서버와의 통신 불안정", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    /**
     * 로그인
     * authenticate() 메서드를 이용한 로그인 */
    private fun startNaverLogin(){
        var naverToken :String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                Log.d("hellogogogo",response.toString())
                val tmpId = response.profile?.id
                val tmpPI:String? = response.profile?.profileImage

                LoginViewModel.providerId = tmpId ?: ""
                LoginViewModel.provider = "NAVER"
                LoginViewModel.profileUrl = tmpPI

                LoginViewModel.login { resultCode ->
                    when (resultCode) {
                        1 -> {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        2 -> {
                            findNavController().navigate(R.id.action_loginFragment_to_nameFragment)
                        }
                        else -> {
                            Toast.makeText(requireContext(), "서버와의 통신 불안정", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(requireContext(), "errorCode: ${errorCode}\n" +"errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
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
                LoginViewModel.socialAccessToken = naverToken.toString()
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

        NaverIdLoginSDK.authenticate(requireContext(), oauthLoginCallback)
    }
}