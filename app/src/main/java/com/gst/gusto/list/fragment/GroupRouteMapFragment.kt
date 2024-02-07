package com.gst.gusto.list.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.R
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.FragmentListGroupMRouteMapBinding
import com.gst.gusto.list.adapter.RouteItem
import com.gst.gusto.list.adapter.RouteMapDetailItem
import com.gst.gusto.list.adapter.RouteViewPagerAdapter
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPointBounds
import net.daum.mf.map.api.MapView

class GroupRouteMapFragment : Fragment(),MapView.POIItemEventListener,MapView.MapViewEventListener {

    lateinit var binding: FragmentListGroupMRouteMapBinding
    lateinit var page : String
    private val TAG = "MapViewEventListener"
    lateinit var mapView : MapView
    private val routePOIList = ArrayList<MapPOIItem>()
    val markerList = ArrayList<MarkerItem>()
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
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 페이지가 선택되었을 때의 작업 수행
                Log.d(TAG, "Page selected: $position")
                val mapPoint = MapPoint.mapPointWithGeoCoord(markerList[position].latitude, markerList[position].longitude)
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(mapPoint,mapView.zoomLevelFloat))
            }
        })

    }

    override fun onResume() {
        super.onResume()

        markerList.add(MarkerItem(0, 0,37.6215101, 127.0751410))
        markerList.add(MarkerItem(0, 1,37.6245301, 127.0740210))
        markerList.add(MarkerItem(0, 2,37.6215001, 127.0743010))

        mapView = MapView(requireContext())
        mapView.setPOIItemEventListener(this)
        mapView.setMapViewEventListener(this)


        mapUtil.setMapInit(mapView, binding.kakaoRouteMap, requireContext(), requireActivity(),"route")
        mapUtil.setRoute(mapView, markerList)

    }
    override fun onPause() {
        super.onPause()
        Log.d("MapViewEventListener","onPause")
        binding.kakaoRouteMap.removeAllViews()
    }

    override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        // 마커 클릭 시 이벤트
        if(poiItem!=null)
            Log.d("MapViewEventListener","${poiItem.itemName}")

        binding.vpSlider.visibility = View.VISIBLE
        if (poiItem != null) {
            binding.vpSlider.currentItem = poiItem.itemName.toInt()
        }
    }
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {}
    override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {}


    override fun onMapViewInitialized(p0: MapView?) {
        Log.d(TAG, "MapView가 초기화되었습니다.")
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도의 중심점이 이동되었습니다.")
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        Log.d(TAG, "지도의 줌 레벨이 변경되었습니다. 새로운 줌 레벨: $p1")
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도가 단일 탭(클릭)되었습니다.")
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도가 더블 탭(클릭)되었습니다.")
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도가 길게 눌렸습니다.")
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        binding.vpSlider.visibility=View.GONE
        Log.d(TAG, "지도 드래그가 시작되었습니다.")
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도 드래그가 종료되었습니다.")
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도 이동이 완료되었습니다.")
    }
}