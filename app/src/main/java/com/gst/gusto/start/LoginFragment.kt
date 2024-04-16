package com.gst.gusto.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.api.LoginViewModel
import com.gst.gusto.BuildConfig
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.StartFragmentLoginBinding
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import java.io.File
import java.net.URI

class LoginFragment : Fragment() {

    lateinit var binding: StartFragmentLoginBinding
    private val LoginViewModel : LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentLoginBinding.inflate(inflater, container, false)

        val naverClientId = getString(R.string.social_login_info_naver_client_id)
        val naverClientSecret = getString(R.string.social_login_info_naver_client_secret)
        val naverClientName = getString(R.string.social_login_info_naver_client_name)
        NaverIdLoginSDK.initialize(requireContext(), naverClientId, naverClientSecret , naverClientName)

        binding.btnNaver.setOnClickListener {
            //Log.d("안녕", BuildConfig.API_BASE)
            startNaverLogin()

        }
        binding.btnNoLogin.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return binding.root

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