package com.gst.gusto.my

import android.content.Intent
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
import com.gst.gusto.my.activity.MyProfileEditActivity
import com.gst.gusto.my.activity.MySettingActivity
import com.gst.gusto.start.StartActivity

class MyFragment : Fragment() {

    lateinit var binding: FragmentMyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(inflater, container, false)
        initViewPager()


        binding.apply{
            btnOption.setOnClickListener {
                // findNavController().navigate(R.id.action_myFragment_to_myProfileFragment)
                val intent = Intent(requireContext(), MySettingActivity::class.java)
                startActivity(intent)
            }
            btnProfileEdit.setOnClickListener {
                val intent = Intent(requireContext(), MyProfileEditActivity::class.java)
                startActivity(intent)
            }
            btnFollowingList.setOnClickListener {
                findNavController().navigate(R.id.action_myFragment_to_followList)
            }
            //임시 리뷰 추가 화면
            btnAddReviewTmp.setOnClickListener {
                findNavController().navigate(R.id.action_myFragment_to_reviewAdd1Fragment)
            }
            //임시 피드에서 리뷰 정보 보기
            btnFeedDetailTmp.setOnClickListener {
                findNavController().navigate(R.id.action_myFragment_to_feedDetail)
            }
            //임시 로그인
            btnLogin.setOnClickListener {
                val intent = Intent(requireContext(), StartActivity::class.java)
                startActivity(intent)
            }
        }
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