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

    val icons = listOf(R.drawable.gallery_review_img, R.drawable.calendar_review_img, R.drawable.calendar_review_img)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentReviewBinding.inflate(inflater, container, false)
        initViewPager()

        return binding.root

    }

    override fun onResume() {
        super.onResume()
        initViewPager()
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

        // ViewPager TabLayout 연결
        TabLayoutMediator(binding.reviewTab, binding.reviewVP) { tab, position ->
            tab.setIcon(icons[position])
        }.attach()

    }

}