package com.gst.gusto.start

import android.content.Intent
import android.net.Uri
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.gst.gusto.api.LoginViewModel
import com.gst.gusto.BuildConfig
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.databinding.StartFragmentLoginBinding
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
import java.io.File

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
            successGoogleLogin("${account.id}","${account.photoUrl}")
        } catch (e: ApiException) {
            Log.d("GOOGLE_Login","${e.toString()}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentLoginBinding.inflate(inflater, container, false)

        val naverClientId = getString(R.string.social_login_info_naver_client_id)
        val naverClientSecret = getString(R.string.social_login_info_naver_client_secret)
        val naverClientName = getString(R.string.social_login_info_naver_client_name)
        NaverIdLoginSDK.initialize(requireContext(), naverClientId, naverClientSecret , naverClientName)

        KakaoSdk.init(requireContext(), BuildConfig.KAKAO_NATIVE_APP_KEY)

        binding.btnNaver.setOnClickListener {
            startNaverLogin()
        }
        binding.btnKakao.setOnClickListener {
            startKakaoLogin()
        }
        binding.btnGoogle.setOnClickListener {
            startGoogleLogin()
        }
        binding.btnNoLogin.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("https://www.googleapis.com/auth/pubsub"))
            .requestEmail() // 이메일도 요청할 수 있다.
            .build()

        return GoogleSignIn.getClient(requireActivity(), googleSignInOption)
    }

    private fun startGoogleLogin() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1000 -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.result!!

                    Log.d("PASS", account.email ?: "")
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun successGoogleLogin(id: String, photoUrl: String) {
        LoginViewModel.providerId = id
        LoginViewModel.profileImg =  File(photoUrl)
        LoginViewModel.provider = "GOOGLE"
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
    private fun startKakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KAKAO", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                successKakaoLogin()
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
                    successKakaoLogin()
                    Log.i("KAKAO", "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
        }
    }
    private fun successKakaoLogin() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
            } else if (user != null) {
                Log.e("KAKAO", "사용자 정보 요청 성공 : $user")
                LoginViewModel.providerId = user.id.toString()
                LoginViewModel.profileImg =  File(user.kakaoAccount?.profile?.profileImageUrl ?: "")
                LoginViewModel.provider = "KAKAO"
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                LoginViewModel.profileImg =  File(tmpPI)
                LoginViewModel.provider = "NAVER"

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
                LoginViewModel.setAccessToken(naverToken.toString())
                LoginViewModel.setRefreshToken(NaverIdLoginSDK.getRefreshToken().toString())

                var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
                var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
                var naverTokenType = NaverIdLoginSDK.getTokenType()
                var naverState = NaverIdLoginSDK.getState().toString()

                Log.d("helpgogogo",
                    naverExpiresAt+", "+naverTokenType+", "+naverState)
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