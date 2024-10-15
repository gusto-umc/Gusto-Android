package com.gst.gusto.start

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
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


        val appUpdateManager = AppUpdateManagerFactory.create(requireContext())
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.d("application versin", appUpdateInfo.toString())
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE)) {
                // 유효한 업데이트가 있을 때
                // ex. 스토어에서 앱 업데이트하기
                val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_one_button, null)
                val mBuilder = AlertDialog.Builder(requireContext())
                    .setView(mDialogView)
                    .setCancelable(false)
                    .create()


                mBuilder?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mBuilder?.window?.requestFeature(Window.FEATURE_NO_TITLE)
                mBuilder.show()

                mDialogView.findViewById<TextView>(R.id.tv_dialog_one_text).text = "새로운 업데이트가 있습니다"
                mDialogView.findViewById<TextView>(R.id.tv_dialog_one_desc).visibility = View.GONE
                //팝업 타이틀 설정, 버튼 작용 시스템
                mDialogView.findViewById<TextView>(R.id.btn_dialog_one).setOnClickListener( {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val packageName = requireActivity().packageName
                    intent.data = Uri.parse("market://details?id=${packageName}")
                    startActivity(intent)
                })
            } else {
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
        }.addOnFailureListener {
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

}