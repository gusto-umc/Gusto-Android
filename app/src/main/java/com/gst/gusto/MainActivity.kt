package com.gst.gusto

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions.getExtensionVersion
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.ActivityMainBinding
import com.gst.gusto.feed.FeedFragment
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
        navController.popBackStack()
        navController.navigate(R.id.fragment_map)

        //Log.d("viewmodel","dsasda : ${ getExtensionVersion(Build.VERSION_CODES.R)}")
        navController.addOnDestinationChangedListener { _, destination, _ ->
            var currentDestinationId = destination.id
            val fragmentManager = this.supportFragmentManager
            val fragment = fragmentManager?.findFragmentByTag("FeedSearchFragmentTag")
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit()
            }
            if (previousDestinationId == currentDestinationId) {
                // 현재 목적지와 이전 목적지가 같은 경우
                // 선택한 탭이 이미 화면에 표시 중이므로 초기 화면으로 이동
                while (navController.currentDestination?.id != R.id.fragment_list&&navController.currentDestination?.id != R.id.fragment_map&&
                        navController.currentDestination?.id != R.id.fragment_review&&navController.currentDestination?.id != R.id.fragment_feed&&
                        navController.currentDestination?.id != R.id.fragment_my) {
                    currentDestinationId = -1
                    navController.popBackStack()
                }
                if(currentDestinationId == R.id.fragment_feed) {
                    val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                    Log.d("CurrentFragment", "Current fragment: $currentFragment")
                    if (currentFragment is FeedFragment) {
                        Log.d("viewmodel flag", "${currentFragment}")

                        // 현재 프래그먼트가 여러분이 원하는 프래그먼트 타입인 경우에만 해당 함수를 실행합니다.
                        // 여러분이 만든 프래그먼트의 타입에 따라서 YourFragmentType을 적절히 변경해주세요.
                        (currentFragment as FeedFragment).initView()
                        (currentFragment as FeedFragment).getData()
                    }

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
        gustoViewModel.getTokens()
        gustoViewModel.mainActivity = this

        gustoViewModel.tokenToastData.observe(this, Observer {
            Toast.makeText(this, "토큰을 재 발급 중입니다", Toast.LENGTH_SHORT).show()
        })


    }

    fun setTrans(flag : Boolean){
        if(flag){
            //상태창 투명
            if(Build.VERSION.SDK_INT >= 19) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                if(Build.VERSION.SDK_INT < 21) {
                    setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
                } else {
                    setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
                    window.statusBarColor = Color.TRANSPARENT
                }
            }
        }else{
            //상태창 투명
            if(Build.VERSION.SDK_INT >= 19) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if(Build.VERSION.SDK_INT < 21) {
                    setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
                } else {
                    setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
                }
            }
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val winAttr = window.attributes
        winAttr.flags = if(on) winAttr.flags or bits else winAttr.flags and bits.inv()
        window.attributes = winAttr
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




}
