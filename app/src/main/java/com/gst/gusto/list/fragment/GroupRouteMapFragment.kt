package com.gst.gusto.list.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.DiaLogFragment
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.Util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.RouteList
import com.gst.gusto.databinding.FragmentListGroupMRouteMapBinding
import com.gst.gusto.list.adapter.RouteViewPagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class GroupRouteMapFragment : Fragment(),MapView.POIItemEventListener,MapView.MapViewEventListener {

    lateinit var binding: FragmentListGroupMRouteMapBinding
    private val TAG = "MapViewEventListener"
    lateinit var mapView : MapView
    private var returnList = ArrayList<MarkerItem>()
    private var change = false
    private val gustoViewModel : GustoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteMapBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabList.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabEdit.setOnClickListener {
            val dialogFragment = DiaLogFragment({ selectedItem ->
                // 아이템 클릭 이벤트를 처리하는 코드를 작성합니다.
                when (selectedItem) {
                    1 -> {
                        change = true
                        if(gustoViewModel.removeRoute.size>0) {
                            for(routeListId in gustoViewModel.removeRoute) {
                                gustoViewModel.deleteRouteStore(routeListId) { result ->
                                    when (result) {
                                        1 -> {

                                        }
                                        else -> {
                                            Toast.makeText(context,"서버와의 연결 불안정",Toast.LENGTH_SHORT ).show()
                                        }
                                    }
                                }
                            }
                        }
                        if(gustoViewModel.addRoute.size>0) {
                            val tmpList =ArrayList<RouteList>()
                            var ordinal = gustoViewModel.markerListLiveData.value!!.size - gustoViewModel.addRoute.size +1
                            Log.d("viewmodel","size : ${ordinal}")
                            for(storeId in gustoViewModel.addRoute) {
                                tmpList.add(RouteList(storeId,
                                    ordinal++,null,null,null,null,null))
                            }
                            gustoViewModel.addRouteStore(tmpList) { result ->
                                when (result) {
                                    1 -> {
                                        gustoViewModel.getGroupRouteDetail(gustoViewModel.currentRouteId) { result ->
                                            when (result) {
                                                1 -> {

                                                }
                                                else -> {
                                                    Toast.makeText(context,"서버와의 연결 불안정",Toast.LENGTH_SHORT ).show()
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                        Toast.makeText(context,"서버와의 연결 불안정", Toast.LENGTH_SHORT ).show()
                                    }
                                }
                            }
                        }


                    }
                }
            }, R.layout.bottomsheetdialog_routes, gustoViewModel,requireActivity() as MainActivity)
            dialogFragment.show(parentFragmentManager, dialogFragment.tag)

            //findNavController().navigate(R.id.fragment_group_m_route_edit)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editMode = arguments?.getBoolean("edit", false) ?: false
        if(editMode) {
            //binding.fabEdit.callOnClick()
        }

        val itemList = gustoViewModel.markerListLiveData.value as ArrayList<MarkerItem>
        deepCopy(itemList)

        val viewPager = binding.vpSlider

        // 이미지 슬라이드
        val adapter = RouteViewPagerAdapter(itemList,requireActivity() as MainActivity,0)
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
                val mapPoint = MapPoint.mapPointWithGeoCoord(itemList[position].latitude, itemList[position].longitude)
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(mapPoint,mapView.zoomLevelFloat))
            }
        })

        mapView = MapView(requireContext())
        mapView.setPOIItemEventListener(this)
        mapView.setMapViewEventListener(this)


        mapUtil.setMapInit(mapView, binding.kakaoRouteMap, requireContext(), requireActivity(),"route",this)



        if(gustoViewModel.routeStorTmpData != null) {
            var data = gustoViewModel.routeStorTmpData

            if (data != null) {
                gustoViewModel.addRoute.add(data.storeId.toLong())
                gustoViewModel.getStoreDetailQuick(data.storeId.toLong()) {result, data2 ->
                    when(result) {
                        1 -> {
                            if(data2!=null) {
                                gustoViewModel.markerListLiveData.value!!.add(mapUtil.Companion.MarkerItem(
                                    data.storeId.toLong(),
                                    0,
                                    0,
                                    data2.latitude,
                                    data2.longitude,
                                    data.storeName,
                                    data.address,
                                    false
                                ))
                                mapUtil.setRoute(mapView, gustoViewModel.markerListLiveData.value!!)
                            }
                            binding.fabEdit.callOnClick()
                            gustoViewModel.routeStorTmpData = null
                        }
                        else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        gustoViewModel.markerListLiveData.observe(viewLifecycleOwner, Observer { markers ->
            Log.d("itemList222",markers.toString())
            mapUtil.setRoute(mapView, markers)
        })
    }
    override fun onPause() {
        super.onPause()
        binding.kakaoRouteMap.removeAllViews()
    }
    override fun onDestroy() {
        super.onDestroy()
        gustoViewModel.groupFragment = 1
        gustoViewModel.addRoute.clear()
        gustoViewModel.removeRoute.clear()
        if(!change) {
            gustoViewModel.markerListLiveData.value?.clear()
            for(data in returnList) {
                gustoViewModel.markerListLiveData.value?.add(data)
            }
        }

    }

    override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        // 마커 클릭 시 이벤트
        if(poiItem!=null)
            Log.d("MapViewEventListener","${poiItem.itemName}")

        binding.vpSlider.visibility = View.VISIBLE
        binding.fabEdit.visibility = View.GONE
        binding.fabList.visibility = View.GONE
        if (poiItem != null) {
            binding.vpSlider.currentItem = poiItem.itemName.toInt()-1
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
        binding.fabEdit.visibility = View.VISIBLE
        binding.fabList.visibility = View.VISIBLE
        Log.d(TAG, "지도 드래그가 시작되었습니다.")
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도 드래그가 종료되었습니다.")
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도 이동이 완료되었습니다.")
    }
    fun deepCopy(itemList : ArrayList<MarkerItem>) {
        returnList = ArrayList<MarkerItem>()
        for(data in itemList) {
            returnList.add(MarkerItem(
                data.storeId,
                data.ordinal,data.routeListId,
                data.latitude,
                data.longitude,
                data.storeName,
                data.address,
                data.bookMark
            ))
        }
    }
}