package com.gst.gusto.ListView.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentSaveTabBinding

class SaveTabFragment : Fragment() {

    private lateinit var binding: FragmentSaveTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveTabBinding.inflate(inflater, container, false)
        val view = binding.root

        val tabLayout = binding.tabLayout
        val viewPager2 = binding.viewpager2

        val pagerAdapter = SaveTabPagerAdapter(requireActivity())
        viewPager2.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            // Tab의 이름을 설정
            when (position) {
                0 -> tab.text = "저장된 맛집"
                1 -> tab.text = "저장되지 않은 맛집"
            }
        }.attach()

        return view
    }

    private inner class SaveTabPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2 // 탭 개수
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MapListViewSaveFragment() // 첫 번째 탭
                1 -> MapListViewSaveFragment() // 두 번째 탭
                else -> throw IllegalArgumentException("Invalid tab position")
            }
        }
    }
}

