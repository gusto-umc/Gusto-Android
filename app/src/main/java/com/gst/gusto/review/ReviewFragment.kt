package com.gst.gusto.review

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentReviewBinding
import com.gst.gusto.review.adapter.ReviewAdapter
import com.gst.gusto.review.fragment.CalendarReviewFragment
import com.gst.gusto.review.fragment.GalleryReviewFragment
import com.gst.gusto.review.fragment.ListReviewFragment

class ReviewFragment : Fragment() {

    lateinit var binding: FragmentReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentReviewBinding.inflate(inflater, container, false)
        initViewPager()

        return binding.root

    }

    private fun initViewPager() {
        //ViewPager2 Adapter 셋팅
        var VPAdapter = ReviewAdapter(requireActivity())
        VPAdapter.addFragment(GalleryReviewFragment())
        VPAdapter.addFragment(CalendarReviewFragment())
        VPAdapter.addFragment(ListReviewFragment())

        //Adapter 연결
        binding.reviewVP.apply {
            adapter = VPAdapter

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }
            })
        }


        // ViewPager, TabLayout 연결
        TabLayoutMediator(binding.reviewTab, binding.reviewVP) { tab, position ->
            when (position) {
                0 -> {
                    val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gallery_review_img)
                    iconDrawable?.let { drawable ->
                        drawable.setBounds(0, 0, 24, 24)
                        tab.icon = drawable
                    }
                    tab.icon?.setTint(Color.parseColor("#333333"))
                }
                1 -> {
                    val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.calendar_review_img)
                    iconDrawable?.let { drawable ->
                        drawable.setBounds(0, 0, 24, 24)
                        tab.icon = drawable
                    }
                    tab.icon?.setTint(Color.parseColor("#CBCBCB"))
                }
                2 -> {
                    val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.list_review_img)
                    iconDrawable?.let { drawable ->
                        drawable.setBounds(0, 0, 24, 24)
                        tab.icon = drawable
                    }
                    tab.icon?.setTint(Color.parseColor("#CBCBCB"))
                }
            }
        }.attach()


        binding.reviewTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // 선택된 탭의 아이콘을 변경
                tab?.icon?.setTint(Color.parseColor("#333333"))
            }


            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 선택이 해제된 탭의 아이콘을 변경
                tab?.icon?.setTint(Color.parseColor("#CBCBCB"))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 탭이 다시 선택될 때의 동작 (생략 가능)
                tab?.icon?.setTint(Color.parseColor("#333333"))
            }

        })

        // 초기에 첫 번째 탭을 선택
        binding.reviewTab.getTabAt(0)?.select()

    }

}