package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.gst.gusto.databinding.FragmentMyBinding
import com.gst.gusto.my.MyViewpagerAdapter

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
        viewPager2Adatper.addFragment(MyReviewFragment())
        viewPager2Adatper.addFragment(MyListFragment())

        //Adapter 연결
        binding.viewpager.apply {
            adapter = viewPager2Adatper

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }
            })
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