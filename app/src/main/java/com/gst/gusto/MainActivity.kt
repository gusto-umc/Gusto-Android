package com.gst.gusto

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gst.gusto.databinding.ActivityMainBinding
import net.daum.mf.map.api.MapView
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private val TAG = "SOL_LOG"
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fl_container) as NavHostFragment
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

                Log.d(TAG, "HASH_CODE -> $HASH_CODE")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception -> $e")
        }



    }

    fun getCon(): NavController {
        return navController
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
