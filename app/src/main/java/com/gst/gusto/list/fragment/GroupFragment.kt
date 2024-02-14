package com.gst.gusto.list.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMBinding
import com.gst.gusto.list.adapter.GroupViewpagerAdapter
import com.gst.gusto.list.adapter.LisAdapter
import java.lang.Math.abs

class GroupFragment : Fragment() {

    lateinit var binding: FragmentListGroupMBinding
    lateinit var mPager : ViewPager2
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            val adapter = mPager.adapter as GroupViewpagerAdapter

            binding.btnSave.visibility =View.GONE
            val frag = adapter.getCurrentFragment()
            Log.d("frag",frag.toString())
            if(frag is GroupRoutesFragment) {
                if(frag.getCon().currentDestination !=null && (frag.getCon().currentDestination!!.id == R.id.fragment_group_m_route_stores
                            || frag.getCon().currentDestination!!.id == R.id.fragment_group_m_route_create)) {
                    frag.getCon().navigate(R.id.fragment_group_m_route_routes)
                } else findNavController().popBackStack()
            } else findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener {
            val adapter = mPager.adapter as GroupViewpagerAdapter

            val frag = adapter.getCurrentFragment()
            if(frag is GroupRoutesFragment) {
                if(frag.getCon().currentDestination !=null && frag.getCon().currentDestination!!.id == R.id.fragment_group_m_route_create) {
                    frag.getCon().navigate(R.id.fragment_group_m_route_routes)
                } else findNavController().popBackStack()
            } else findNavController().popBackStack()
            binding.btnSave.visibility =View.GONE
        }
        binding.lyPeople.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_followListFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPager = binding.vpGroup
        mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if(position==0) {
                    var alpha = 1 - abs(positionOffset*1.3)
                    if(alpha>1) alpha = 1.0
                    else if (alpha<0) alpha = 0.0
                    val mainColor = ContextCompat.getColor(requireContext(), R.color.sub_m)

                    val backgroundColor = Color.argb(
                        ((alpha) * 255).toInt(),
                        Color.red(mainColor),
                        Color.green(mainColor),
                        Color.blue(mainColor)
                    )
                    binding.lyGroup.setBackgroundColor(backgroundColor)
                }
            }
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
        gustoViewModel.getGroup {result, data ->
            when(result) {
                1 -> {
                    if(data!=null) {
                        Log.d("viewmodel",data.toString())
                        binding.tvName.text = data.groupName
                        binding.tvComment.text = data.groupScript
                        binding.tvNotice.text = data.notice
                        binding.tvPeople.text = "${data.groupMembers.get(0).nickname} 님 외 ${data.groupMembers.size-1}명"
                        if(data.groupMembers.size==1) {
                            setImage(binding.ivProfileImage1,data.groupMembers.get(0).profileImg,requireContext())
                            binding.cdProfileImage2.visibility = View.INVISIBLE
                            binding.cdProfileImage3.visibility = View.INVISIBLE
                        } else if(data.groupMembers.size==2) {
                            setImage(binding.ivProfileImage1,data.groupMembers.get(0).profileImg,requireContext())
                            setImage(binding.ivProfileImage2,data.groupMembers.get(1).profileImg,requireContext())
                            binding.cdProfileImage3.visibility = View.INVISIBLE
                        } else {
                            setImage(binding.ivProfileImage1,data.groupMembers.get(0).profileImg,requireContext())
                            setImage(binding.ivProfileImage2,data.groupMembers.get(1).profileImg,requireContext())
                            setImage(binding.ivProfileImage3,data.groupMembers.get(2).profileImg,requireContext())
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gustoViewModel.groupFragment = 0
    }

    override fun onPause() {
        super.onPause()
        mPager.adapter = null
    }
    override fun onResume() {
        super.onResume()
        mPager.adapter = GroupViewpagerAdapter(requireActivity(),GroupRoutesFragment(gustoViewModel.groupFragment),mPager,2)
        mPager.setCurrentItem(gustoViewModel.groupFragment,false)
        if(gustoViewModel.groupFragment == 1)  {
            binding.lyGroup.setBackgroundColor(Color.TRANSPARENT)
        }
    }

}