package com.gst.gusto.my

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.gst.clock.Fragment.MyReviewFragment
import com.gst.clock.Fragment.OtherReviewFragment
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMyBinding
import com.gst.gusto.my.activity.MySettingActivity
import com.gst.gusto.my.adapter.MyViewpagerAdapter
import com.gst.gusto.my.fragment.MyListFragment

class OtherFragment : Fragment() {

    lateinit var binding: FragmentMyBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    private val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#F27781"))
    private val colorStateOffList = ColorStateList.valueOf(Color.parseColor("#ECECEC"))
    private var followed = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(inflater, container, false)



        val nickname = gustoViewModel.currentFeedNickname

        gustoViewModel.getUserProfile(nickname) { result, data ->
            when(result) {
                1 -> {
                    if(data!=null) {
                        Log.d("viewmodel",data.toString())
                        setImage(binding.ivProfileImage,data.profileImg,requireContext())
                        gustoViewModel.profileNickname= data.nickname
                        binding.tvNickname.text = data.nickname
                        binding.tvReviewNum.text = "${data.review}"
                        binding.tvFollowingNum.text = "${data.following}"
                        binding.tvFollowerNum.text = "${data.follower}"
                        //setImage(binding.ivProfileImage)
                        followed = data.followed
                        if(data.followed) {
                            binding.btnProfileEdit.backgroundTintList = colorStateOffList
                            binding.btnProfileEdit.text = "팔로잉"
                            binding.btnProfileEdit.setTextColor(Color.parseColor("#717171"))
                        } else {
                            binding.btnProfileEdit.backgroundTintList = colorStateOnList
                            binding.btnProfileEdit.text = "팔로우"
                            binding.btnProfileEdit.setTextColor(Color.parseColor("#FFFFFF"))
                        }
                        initViewPager()
                    }
                }
            }
        }

        binding.apply{
            btnOption.setOnClickListener {
                // findNavController().navigate(R.id.action_myFragment_to_myProfileFragment)
                val intent = Intent(requireContext(), MySettingActivity::class.java)
                startActivity(intent)
            }
            btnProfileEdit.setOnClickListener {
                if(followed) {
                    gustoViewModel.unFollow(nickname) { result ->
                        when(result) {
                            1 -> {
                                followed = false
                                btnProfileEdit.backgroundTintList = colorStateOnList
                                btnProfileEdit.text = "팔로우"
                                tvFollowerNum.text ="${tvFollowerNum.text.toString().toInt()-1}"
                                binding.btnProfileEdit.setTextColor(Color.parseColor("#FFFFFF"))
                            }
                        }
                    }
                } else {
                    gustoViewModel.follow(nickname) { result ->
                        when(result) {
                            1 -> {
                                followed = true
                                btnProfileEdit.backgroundTintList = colorStateOffList
                                btnProfileEdit.text = "팔로잉"
                                tvFollowerNum.text ="${tvFollowerNum.text.toString().toInt()+1}"
                                binding.btnProfileEdit.setTextColor(Color.parseColor("#717171"))
                            }
                        }
                    }

                }
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnProfileEdit.text = "팔로잉"
        binding.btnOption.visibility =View.GONE
    }
    private fun initViewPager() {
        //ViewPager2 Adapter 셋팅
        var viewPager2Adatper = MyViewpagerAdapter(requireActivity())
        viewPager2Adatper.addFragment(OtherReviewFragment())
        viewPager2Adatper.addFragment(MyListFragment())
        viewPager2Adatper.addFragment(MyRouteFragment())
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
                2 -> tab.text = "루트"
            }
        }.attach()
    }

}