package com.gst.gusto.start

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gst.gusto.BuildConfig
import com.gst.gusto.R
import com.gst.gusto.databinding.StartFragmentLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    lateinit var binding: StartFragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentLoginBinding.inflate(inflater, container, false)

        binding.btnNaver.setOnClickListener {
            binding.wv.visibility = View.VISIBLE
            Log.d("안녕", BuildConfig.API_BASE)
            //binding.wv.loadUrl(BuildConfig.API_BASE+"oauth2/authorization/naver")
            findNavController().navigate(R.id.action_loginFragment_to_nameFragment)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}