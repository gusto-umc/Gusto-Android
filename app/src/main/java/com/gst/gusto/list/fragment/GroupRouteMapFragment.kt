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
import com.gst.clock.Fragment.MapFragment
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.FragmentListGroupMRouteMapBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RouteItem
import com.gst.gusto.list.adapter.RouteMapDetailItem
import com.gst.gusto.list.adapter.RouteViewPagerAdapter
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter
import net.daum.mf.map.api.MapView

class GroupRouteMapFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteMapBinding
    lateinit var page : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteMapBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            if(page == "route") {
                findNavController().navigate(R.id.action_groupMRoutMapFragment_to_routeStoresFragment)
            } else {
                val bundle = Bundle()
                bundle.putInt("viewpage",1)
                findNavController().navigate(R.id.action_groupMRoutMapFragment_to_groupFragment,bundle)
            }
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = ArrayList<RouteMapDetailItem>()

        val receivedBundle = arguments
        if (receivedBundle != null) {
            page = receivedBundle.getString("page")?:"group"
            val tmpList = receivedBundle.getSerializable("itemList") as ArrayList<RouteItem>?
            if (tmpList != null) {
                for(data in tmpList) {
                    itemList.add(RouteMapDetailItem(data.name,data.loc,"","",false))
                }
            }
        }



        val viewPager = binding.vpSlider


        // 이미지 슬라이드
        val adapter = RouteViewPagerAdapter(itemList)
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 1
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

    override fun onResume() {
        super.onResume()
        val markerList = ArrayList<MarkerItem>()
        markerList.add(MarkerItem(0, 1,37.6215101, 127.0751410))
        markerList.add(MarkerItem(0, 2,37.6245301, 127.0740210))
        markerList.add(MarkerItem(0, 3,37.6215001, 127.0743010))

        val mapView = MapView(requireContext())

        mapUtil.setMapInit(mapView, binding.kakaoRouteMap, requireContext(), requireActivity(),"route")
        mapUtil.setRoute(mapView, markerList)
    }



}