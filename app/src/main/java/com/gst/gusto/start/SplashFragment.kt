package com.gst.gusto.start

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.databinding.StartFragmentSplashBinding
import com.gst.gusto.util.GustoApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    lateinit var binding: StartFragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartFragmentSplashBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!GustoApplication.prefs.getSharedPrefsBoolean("logout")) {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000) // 2000 밀리초(2초) 동안 지연
                // 여기에 지연 후 수행할 코드 작성
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
            GustoApplication.prefs.setSharedPrefsBoolean("logout",false)
        }else {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }

}