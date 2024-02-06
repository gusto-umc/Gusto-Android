package com.gst.gusto.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gst.api.LoginViewModel
import com.gst.gusto.BuildConfig
import com.gst.gusto.R
import com.gst.gusto.databinding.StartFragmentLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LoginFragment : Fragment() {

    lateinit var binding: StartFragmentLoginBinding
    private val LoginViewModel : LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentLoginBinding.inflate(inflater, container, false)

        binding.wv.apply {
            webViewClient = WebViewClient()
            clearCache(true)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }

        binding.btnNaver.setOnClickListener {
            binding.wv.visibility = View.VISIBLE
            Log.d("안녕", BuildConfig.API_BASE)
            binding.wv.loadUrl(BuildConfig.API_BASE+"oauth/authorize/naver")
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.wv.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                var url = request?.url.toString()
                Log.d("url",url)
                if (url.startsWith("http://119.192.42.243:10004/auth/result")) {
                    getResponseLogin(url) { result ->
                        when (result) {
                            1 -> {
                                findNavController().navigate(R.id.action_loginFragment_to_nameFragment)
                            }
                        }
                    }
                    return true
                } else {
                    binding.wv.loadUrl(url)
                    return false
                }
            }
            override fun onPageFinished(view: WebView, url: String) {

            }
        })
    }
    private fun getResponseLogin(url : String, callback: (Int) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val headers = fetchHeadersFromUrl(url)
            withContext(Dispatchers.Main) {
                for ((key, value) in headers) {
                    if(key == "temp-token") {
                        LoginViewModel.setTempToken(value.get(0))
                    }
                }
                callback(1)
            }
        }
    }
    private fun fetchHeadersFromUrl(urlString: String): Map<String, List<String>> {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        try {
            // 요청 보내기 (헤더 정보를 얻기 위해 빈 응답을 받아옴)
            connection.requestMethod = "HEAD"
            connection.connect()

            // 헤더 정보 가져오기
            return connection.headerFields
        } finally {
            connection.disconnect()
        }
    }
}