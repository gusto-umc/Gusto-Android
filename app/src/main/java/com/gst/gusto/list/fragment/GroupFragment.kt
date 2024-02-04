package com.gst.gusto.list.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentListGroupMBinding
import com.gst.gusto.list.adapter.GroupViewpagerAdapter
import java.lang.Math.abs

class GroupFragment : Fragment() {

    lateinit var binding: FragmentListGroupMBinding
    lateinit var mPager : ViewPager2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            val adapter = mPager.adapter as GroupViewpagerAdapter

            val frag = adapter.getCurrentFragment()
            Log.d("hello1",frag.toString())
            if(frag is GroupRoutesFragment ) {

                if(frag.getCon().currentDestination !=null && frag.getCon().currentDestination!!.id == R.id.fragment_group_m_route_stores) {

                    frag.getCon().navigate(R.id.fragment_group_m_route_routes)
                } else findNavController().navigate(R.id.action_groupFragment_to_listFragment)
            } else findNavController().navigate(R.id.action_groupFragment_to_listFragment)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments

        val data = bundle?.getInt("viewpage", 0)?: 0 // "key"에 해당하는 값을 가져옴

        mPager = binding.vpGroup


        var frag2 : Fragment
        if(data == 1)  {
            frag2 = GroupRoutesFragment(1)
            binding.lyGroup.setBackgroundColor(Color.TRANSPARENT)
        }
        else frag2 = GroupRoutesFragment(0)

        mPager.adapter = GroupViewpagerAdapter(requireActivity(),frag2,mPager,2)

        mPager.setCurrentItem(data,false)



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
    }

}