package com.gst.gusto.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.gst.clock.Fragment.MyListFragment
import com.gst.clock.Fragment.MyReviewFragment
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMyBinding

class MyFragment : Fragment() {

    lateinit var binding: FragmentMyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(inflater, container, false)
        initViewPager()
        return binding.root

    }
    private fun initViewPager() {
        //ViewPager2 Adapter 셋팅
        var viewPager2Adatper = MyViewpagerAdapter(requireActivity())
        viewPager2Adatper.addFragment(MyListFragment())
        viewPager2Adatper.addFragment(MyReviewFragment())

        //Adapter 연결
        binding.viewpager.apply {
            adapter = viewPager2Adatper

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }
            })
        }

        //임시 리뷰 추가 화면
        binding.btnAddReviewTmp.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_reviewAdd1Fragment)
        }

        //ViewPager, TabLayout 연결
        TabLayoutMediator(binding.tablayout, binding.viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = "리뷰"
                1 -> tab.text = "찜 리스트"
            }
        }.attach()
    }

}