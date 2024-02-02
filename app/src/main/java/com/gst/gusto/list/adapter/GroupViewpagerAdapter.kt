package com.gst.gusto.list.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.R
import com.gst.gusto.list.fragment.GroupRoutesFragment
import com.gst.gusto.list.fragment.GroupStoresFragment

class GroupViewpagerAdapter(fa: FragmentActivity,private val frag2: Fragment,private val viewPager: ViewPager2, private val count: Int) : FragmentStateAdapter(fa) {

    private var currentFragment: Fragment? = null

    init {
        // ViewPager2의 페이지 변경을 감지하는 콜백 등록

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // 페이지가 선택될 때 현재 포커스 중인 Fragment 갱신
                currentFragment = when (position) {
                    0 -> GroupStoresFragment()
                    1 -> frag2
                    else -> null
                }
            }
        })
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                GroupStoresFragment()
            }
            else -> {
                frag2
            }
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
    fun getCurrentFragment(): Fragment? {
        return currentFragment
    }
}

