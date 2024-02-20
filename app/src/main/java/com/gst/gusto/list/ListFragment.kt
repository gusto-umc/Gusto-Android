package com.gst.gusto.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.gst.clock.Fragment.ListGroupFragment
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.DiaLogFragment
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListMainBinding


class ListFragment : Fragment() {
    lateinit var binding: FragmentListMainBinding
    private var isFabOpen = false
    private val colorStateOffList = ColorStateList.valueOf(Color.parseColor("#FEB520"))
    private val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
    lateinit var fabBackground : ImageView
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var navHostFragment: NavHostFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListMainBinding.inflate(inflater, container, false)

        val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#FEB520"))
        val colorStateOffList = ColorStateList.valueOf(Color.parseColor("#F3F3F3"))
        binding.btnGroup.setOnClickListener {
            ViewCompat.setBackgroundTintList(binding.btnGroup, colorStateOnList)
            binding.ivGroup.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.tvGroup.setTextColor(Color.parseColor("#FFFFFF"))

            ViewCompat.setBackgroundTintList(binding.btnRoute, colorStateOffList)
            binding.ivRoute.setColorFilter(Color.parseColor("#FFD704"))
            binding.tvRoute.setTextColor(Color.parseColor("#828282"))
            navHostFragment.navController.navigate(R.id.fragment_list_group)
        }

        binding.btnRoute.setOnClickListener {
            if(isFabOpen)
                toggleFab()
            ViewCompat.setBackgroundTintList(binding.btnRoute, colorStateOnList)
            binding.ivRoute.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.tvRoute.setTextColor(Color.parseColor("#FFFFFF"))

            ViewCompat.setBackgroundTintList(binding.btnGroup, colorStateOffList)
            binding.ivGroup.setColorFilter(Color.parseColor("#FFD704"))
            binding.tvGroup.setTextColor(Color.parseColor("#828282"))

            navHostFragment.navController.navigate(R.id.fragment_list_route)
        }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setFABClickEvent()
        navHostFragment = childFragmentManager.findFragmentById(R.id.fl_list_container) as NavHostFragment

        fabBackground = binding.ivFabBackground

        navHostFragment.navController.popBackStack()
        navHostFragment.navController.navigate(R.id.fragment_list_group)

    }

    override fun onResume() {
        super.onResume()
        if(gustoViewModel.listFragment=="route") binding.btnRoute.callOnClick()
    }
    private fun setFABClickEvent() {
        // 플로팅 버튼 클릭시 애니메이션 동작 기능
        binding.fabMain.setOnClickListener {
            if(binding.tvRoute.currentTextColor == Color.parseColor("#FFFFFF")) {
                findNavController().navigate(R.id.action_listFragment_to_routeCreateFragment)
            } else {
                toggleFab()
            }
        }
        binding.fabMain.backgroundTintList = colorStateOffList

        // 플로팅 버튼 클릭 이벤트 - 캡처
        binding.fabCreate.setOnClickListener {
            toggleFab()
            val dialogFragment = DiaLogFragment({ selectedItem ->
                // 아이템 클릭 이벤트를 처리하는 코드를 작성합니다.
                when (selectedItem) {
                    1 -> {
                        (navHostFragment.childFragmentManager.primaryNavigationFragment as ListGroupFragment).checkGroups()
                    }
                }
            }, R.layout.bottomsheetdialog_create, gustoViewModel,requireActivity() as MainActivity)
            dialogFragment.show(parentFragmentManager, dialogFragment.tag)
        }

        // 플로팅 버튼 클릭 이벤트 - 공유
        binding.fabInput.setOnClickListener {
            toggleFab()
            val dialogFragment = DiaLogFragment({ selectedItem ->
                // 아이템 클릭 이벤트를 처리하는 코드를 작성합니다.
                when (selectedItem) {
                    1 -> {
                        (navHostFragment.childFragmentManager.primaryNavigationFragment as ListGroupFragment).checkGroups()
                    }
                }
            }, R.layout.bottomsheetdialog_join, gustoViewModel,requireActivity() as MainActivity)
            dialogFragment.show(parentFragmentManager, dialogFragment.tag)
        }
    }
    private fun toggleFab() {

        binding.fabMain.isClickable = false
        binding.fabInput.isClickable = false
        binding.fabCreate.isClickable = false
        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션
        if (isFabOpen) {
            collapseView()
            ObjectAnimator.ofFloat(binding.fabInput, "translationY", dpToPixels(0f,resources.displayMetrics)).apply { start() }
            ObjectAnimator.ofFloat(binding.fabCreate, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 45f, 0f).apply { start() }
            binding.fabMain.backgroundTintList = colorStateOffList
            binding.fabMain.imageTintList = colorStateOnList
            binding.fabMain.rippleColor = Color.parseColor("#FFFFFF")

        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            expandView()
            ObjectAnimator.ofFloat(binding.fabInput, "translationY", dpToPixels(-128f,resources.displayMetrics)).apply { start() }
            ObjectAnimator.ofFloat(binding.fabCreate, "translationY", dpToPixels(-64f,resources.displayMetrics)).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 0f, 45f).apply { start() }
            binding.fabMain.backgroundTintList = colorStateOnList
            binding.fabMain.imageTintList = colorStateOffList
            binding.fabMain.rippleColor = Color.parseColor("#FEB520")
        }

        isFabOpen = !isFabOpen

    }
    private fun expandView() {
        val startHeight = fabBackground.height
        val endHeight = startHeight + dpToPixels(128f, resources.displayMetrics)

        val animator = ValueAnimator.ofInt(startHeight, endHeight.toInt())
        animator.interpolator = DecelerateInterpolator()

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = fabBackground.layoutParams
            layoutParams.height = value
            fabBackground.layoutParams = layoutParams
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.fabMain.isClickable = true
                binding.fabInput.isClickable = true
                binding.fabCreate.isClickable = true
            }
        })

        animator.start()
    }
    private fun collapseView() {
        val startHeight = fabBackground.height
        val endHeight = startHeight - dpToPixels(128f, resources.displayMetrics)

        val animator = ValueAnimator.ofInt(startHeight, endHeight.toInt())
        animator.interpolator = AccelerateInterpolator()

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = fabBackground.layoutParams
            layoutParams.height = value
            fabBackground.layoutParams = layoutParams
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.fabMain.isClickable = true
                binding.fabInput.isClickable = true
                binding.fabCreate.isClickable = true
            }
        })
        animator.start()
    }

    fun callBtnGroup() {
        binding.btnGroup.callOnClick()
    }

}