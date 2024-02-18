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
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.ActivityMainBinding
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    val gustoViewModel : GustoViewModel by viewModels()
    private var previousDestinationId: Int = -1
    private val TAG = "SOL_LOG"
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fl_container) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            var currentDestinationId = destination.id
            if (previousDestinationId == currentDestinationId) {
                // 현재 목적지와 이전 목적지가 같은 경우
                // 선택한 탭이 이미 화면에 표시 중이므로 초기 화면으로 이동
                while (navController.currentDestination?.id != R.id.fragment_list&&navController.currentDestination?.id != R.id.fragment_map&&
                        navController.currentDestination?.id != R.id.fragment_review&&navController.currentDestination?.id != R.id.fragment_feed&&
                        navController.currentDestination?.id != R.id.fragment_my) {
                    currentDestinationId = -1
                    navController.popBackStack()
                }
            }
            // 이전 목적지 업데이트
            previousDestinationId = currentDestinationId
        }
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item->
            item.onNavDestinationSelected(navController)
            true
        }
        // 카카오 해쉬키 얻기
        try {
            val information = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = information.signingInfo.apkContentsSigners
            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA").apply {
                    update(signature.toByteArray())
                }
                val HASH_CODE = String(Base64.encode(md.digest(), 0))

                /*val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clipData = ClipData.newPlainText("viewmodel_data", " $HASH_CODE")

                clipboardManager.setPrimaryClip(clipData)*/
                Log.d(TAG, "HASH_CODE -> $HASH_CODE")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception -> $e")
        }
        gustoViewModel.getTokens(this)
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
