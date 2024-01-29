package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.FragmentListGroupMRouteMapBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RouteItem
import com.gst.gusto.list.adapter.RouteMapDetailItem
import com.gst.gusto.list.adapter.RouteViewPagerAdapter
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter

class GroupRouteMapFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteMapBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("viewpage",1)
            findNavController().navigate(R.id.action_groupMRoutMapFragment_to_groupFragment,bundle)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = ArrayList<RouteMapDetailItem>()

        itemList.add(RouteMapDetailItem("구스또 레스토랑","메롱시 메로나동 바밤바 24-6 1층", "매주 월요일 휴뮤, 08:~15:00","010-5338-8662",false))
        itemList.add(RouteMapDetailItem("구스또 레스토랑2","메롱시 메로나동 바밤바 24-6 1층", "매주 월요일 휴뮤, 08:~15:00","010-5338-8662",false))
        itemList.add(RouteMapDetailItem("구스또 레스토랑3","메롱시 메로나동 바밤바 24-6 1층", "매주 월요일 휴뮤, 08:~15:00","010-5338-8662",false))
        itemList.add(RouteMapDetailItem("구스또 레스토랑4","메롱시 메로나동 바밤바 24-6 1층", "매주 월요일 휴뮤, 08:~15:00","010-5338-8662",false))

        val viewPager = binding.vpSlider


        // 이미지 슬라이드
        val adapter = RouteViewPagerAdapter(itemList)
        viewPager.adapter = adapter

        //viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER)

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(
            MarginPageTransformer(
            util.dpToPixels(4f, resources.displayMetrics).toInt()
        )
        )
        compositePageTransformer.addTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {

            }
        })
        viewPager.setPageTransformer(compositePageTransformer)

    }



}