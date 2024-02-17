package com.gst.gusto

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.ActivityMainBinding
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    val gustoViewModel : GustoViewModel by viewModels()
    private val TAG = "SOL_LOG"
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fl_container) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)

        // 카카오 해쉬키 얻기
        try {
            val information = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = information.signingInfo.apkContentsSigners
            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA").apply {
                    update(signature.toByteArray())
                }
                val HASH_CODE = String(Base64.encode(md.digest(), 0))

                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clipData = ClipData.newPlainText("viewmodel_data", " $HASH_CODE")

                clipboardManager.setPrimaryClip(clipData)
                Log.d(TAG, "HASH_CODE -> $HASH_CODE")
            }


        } catch (e: Exception) {
            Log.d(TAG, "Exception -> $e")
        }
    }

    fun getCon(): NavController {
        return navController
    }
    fun getNavHost() : NavHostFragment {
        return navHostFragment
    }
    fun getViewModel() : GustoViewModel {
        return gustoViewModel
    }
    fun hideBottomNavigation(bool : Boolean) {
        val bottomNavigation = binding.bottomNavigationView
        if(bool){
            bottomNavigation.isGone = true
        }
        else {
            bottomNavigation.isVisible = true
        }
    }
    fun popFragment() {
        supportFragmentManager.popBackStack()
    }
    fun getSharedPref(): Pair<String, String> {
        val sharedPref = getSharedPreferences("token_pref", Context.MODE_PRIVATE)
        // 액세스 토큰과 리프레시 토큰을 가져오는 함수
        val accessToken = sharedPref.getString("accessToken", "")?: ""
        val refreshToken = sharedPref.getString("refreshToken", "")?: ""
        return Pair(accessToken, refreshToken)
    }
}
