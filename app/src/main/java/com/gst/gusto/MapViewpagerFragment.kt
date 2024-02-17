package com.gst.gusto


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.Util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMapViewpagerBinding
import com.gst.gusto.list.adapter.RouteViewPagerAdapter
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapViewpagerFragment : Fragment(), MapView.POIItemEventListener,MapView.MapViewEventListener{

    lateinit var binding: FragmentMapViewpagerBinding
    lateinit var mapView : MapView
    private val gustoViewModel : GustoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapViewpagerBinding.inflate(inflater, container, false)

        // 클릭 리스너 설정
        binding.listViewBtn.setOnClickListener {
            // 버튼이 클릭되었을 때 실행될 동작
            Toast.makeText(requireContext(), "목록보기 버튼이 클릭되었습니다.", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("viewmodel","hi")
        val viewPager = binding.vpSlider

        val markers = ArrayList<mapUtil.Companion.MarkerItem>()
        // 이미지 슬라이드
        val adapter = RouteViewPagerAdapter(markers,requireActivity() as MainActivity,1)
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
                val mapPoint = MapPoint.mapPointWithGeoCoord(markers[position].latitude, markers[position].longitude)
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(mapPoint,mapView.zoomLevelFloat))
            }
        })

        mapView = MapView(requireContext())
        mapView.setPOIItemEventListener(this)
        mapView.setMapViewEventListener(this)

        mapUtil.setMapInit(mapView, binding.kakaoMapSearch, requireContext(), requireActivity(),"route")

        var num = 2
        for( id in gustoViewModel.storeIdList) {
            var selectAfter = 0
            gustoViewModel.getStoreDetailQuick(id) {result, data ->
                when(result) {
                    1 -> {
                        if (data != null) {
                            val tmpData = mapUtil.Companion.MarkerItem(
                                data.storeId,
                                num++,
                                0,
                                data.latitude,
                                data.longitude,
                                data.storeName,
                                data.address,
                                data.pin
                            )
                            if(data.storeId == gustoViewModel.selectStoreId) {
                                tmpData.ordinal = 1
                                num--
                                markers.add(0,tmpData)
                            } else markers.add(tmpData)
                            Log.d("viewmodel","${tmpData.toString()}, ${markers.size+2}, ${gustoViewModel.storeIdList.size-1}")
                        }
                        if (markers.size == gustoViewModel.storeIdList.size) {
                            Log.d("viewmodel","set marker!!!")
                            mapUtil.setStores(mapView, markers)
                            adapter.notifyDataSetChanged()
                        }

                    }
                    else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
    override fun onPause() {
        super.onPause()
        binding.kakaoMapSearch.removeAllViews()
    }
    override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        // 마커 클릭 시 이벤트
        binding.vpSlider.visibility = View.VISIBLE

        if (poiItem != null) {
            binding.vpSlider.currentItem = poiItem.itemName.toInt()-1
        }
    }
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {}
    override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {}


    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        binding.vpSlider.visibility=View.GONE
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }
}
