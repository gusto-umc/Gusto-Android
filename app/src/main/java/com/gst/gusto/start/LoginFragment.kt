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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
            //findNavController().navigate(R.id.action_loginFragment_to_nameFragment)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.wv.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                var url = request?.url.toString()
                Log.d("url",url)
                if (url.startsWith("http://gusto")) {
                    val urlConnection: String = request?.url.toString()
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val finalUrl = resolveRedirect(urlConnection)

                            val url = URL(finalUrl)
                            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                            connection.connectTimeout = 3000
                            connection.connect()

                            // Get the size of the file which is in the header of the request
                            val size: Int = connection.contentLength
                            Log.d("ConnectionStatus", "Response Code: ${connection.responseCode}")
                            Log.d("ConnectionStatus", "Response Message: ${connection.responseMessage}")
                            Log.d("ConnectionStatus", "Response Message: ${connection}")
                            val headers = connection.headerFields
                            for ((key, value) in headers) {
                                Log.d("Headers", "$key: $value")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
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
    private fun resolveRedirect(url: String): String {
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = false
        connection.connect()

        val redirectUrl: String = connection.getHeaderField("Location") ?: url

        return if (redirectUrl.startsWith("http")) {
            // Absolute URL
            redirectUrl
        } else {
            // Relative URL, resolve against the original URL
            val originalUrl = URL(url)
            val resolvedUrl = URL(originalUrl, redirectUrl)
            resolvedUrl.toString()
        }
    }
}