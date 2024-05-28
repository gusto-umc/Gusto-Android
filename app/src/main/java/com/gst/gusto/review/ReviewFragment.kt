package com.gst.gusto.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewBinding
import com.gst.gusto.review.adapter.ReviewAdapter
import com.gst.gusto.review.fragment.CalendarReviewFragment
import com.gst.gusto.review.fragment.GalleryReviewFragment
import com.gst.gusto.review.fragment.ListReviewFragment

class ReviewFragment : Fragment() {

    lateinit var binding: FragmentReviewBinding

    val icons = listOf(R.drawable.gallery_review_img, R.drawable.calendar_review_img, R.drawable.list_review_img)

    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentReviewBinding.inflate(inflater, container, false)
        initViewPager()
        if(gustoViewModel.currentReviewPage != 0){
            binding.reviewVP.currentItem = gustoViewModel.currentReviewPage
        }
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        if(gustoViewModel.currentReviewPage != 0){
            binding.reviewVP.currentItem = gustoViewModel.currentReviewPage
        }
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
                    gustoViewModel.currentReviewPage = binding.reviewVP.currentItem
                }

            })
        }

        // ViewPager TabLayout 연결
        TabLayoutMediator(binding.reviewTab, binding.reviewVP) { tab, position ->
            tab.setIcon(icons[position])
        }.attach()

        // 각 탭에 OnClickListener 설정
        for (i in 0 until binding.reviewTab.tabCount) {
            val tab = binding.reviewTab.getTabAt(i)
            tab?.view?.setOnClickListener {
                binding.reviewVP.currentItem = i
                gustoViewModel.currentReviewPage = i
            }
        }

    }

}
