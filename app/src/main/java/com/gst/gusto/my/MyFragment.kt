package com.gst.gusto.my

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.gst.clock.Fragment.MyReviewFragment
import com.gst.gusto.R
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMyBinding
import com.gst.gusto.my.activity.MyProfileEditActivity
import com.gst.gusto.my.activity.MySettingActivity
import com.gst.gusto.my.fragment.MyListFragment
import com.gst.gusto.my.viewmodel.MyReviewViewModel
import com.gst.gusto.my.viewmodel.MyReviewViewModelFactory

class MyFragment : Fragment() {

    lateinit var binding: FragmentMyBinding

    private val gustoViewModel : GustoViewModel by activityViewModels()
    private val myReviewFragment: MyReviewViewModel by viewModels { MyReviewViewModelFactory() }

    private var followed = false

    private val myFragmentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            gustoViewModel.getUserProfile("my") { result, data ->
                when(result) {
                    1 -> {
                        if(data!=null) {
                            Log.d("viewmodel",data.toString())
                            setImage(binding.ivProfileImage,data.profileImg,requireContext())
                            gustoViewModel.profileNickname = ""
                            binding.tvNickname.text = data.nickname
                            binding.tvReviewNum.text = data.review.toString()
                            binding.tvReviewNum.text = "${data.review}"
                            binding.tvFollowingNum.text = "${data.following}"
                            binding.tvFollowerNum.text = "${data.follower}"
                            //setImage(binding.ivProfileImage)
                            followed = data.followed
                        }
                    }
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(inflater, container, false)

        gustoViewModel.getUserProfile("my") { result, data ->
            when(result) {
                1 -> {
                    if(data!=null) {
                        Log.d("viewmodel",data.toString())
                        setImage(binding.ivProfileImage,data.profileImg,requireContext())
                        gustoViewModel.profileNickname = ""
                        binding.tvNickname.text = data.nickname
                        binding.tvReviewNum.text = data.review.toString()
                        binding.tvReviewNum.text = "${data.review}"
                        binding.tvFollowingNum.text = "${data.following}"
                        binding.tvFollowerNum.text = "${data.follower}"
                        //setImage(binding.ivProfileImage)
                        followed = data.followed
                    }
                }
            }
        }

        binding.apply{
            btnOption.setOnClickListener {
                // findNavController().navigate(R.id.action_myFragment_to_myProfileFragment)
                val intent = Intent(requireContext(), MySettingActivity::class.java)
                myFragmentLauncher.launch(intent)
            }
            btnProfileEdit.setOnClickListener {
                val intent = Intent(requireContext(), MyProfileEditActivity::class.java)
                myFragmentLauncher.launch(intent)
            }
            btnFollowerList.setOnClickListener {
                gustoViewModel.followListTitleName= "팔로워"
                findNavController().navigate(R.id.action_myFragment_to_followList)
            }
            btnFollowingList.setOnClickListener {
                gustoViewModel.followListTitleName= "팔로잉"
                findNavController().navigate(R.id.action_myFragment_to_followList)
            }
            btnBack.setOnClickListener {
                //findNavController().popBackStack()
            }
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun initViewPager() {

        //ViewPager2 Adapter 셋팅
        var viewPager2Adatper = MyViewpagerAdapter(this)
        viewPager2Adatper.addFragment(MyReviewFragment())
        viewPager2Adatper.addFragment(MyListFragment())
        viewPager2Adatper.addFragment(MyRouteFragment())

        // Adapter 연결

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