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
import com.gst.gusto.util.DiaLogFragment
import com.gst.gusto.util.mapUtil
import com.gst.gusto.util.mapUtil.Companion.MarkerItem
import com.gst.gusto.util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.RouteList
import com.gst.gusto.databinding.FragmentListGroupMRouteMapBinding
import com.gst.gusto.list.adapter.RouteViewPagerAdapter
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GroupRouteMapFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteMapBinding
    private val TAG = "MapViewEventListener"
    private var returnList = ArrayList<MarkerItem>()
    private var change = false
    private val gustoViewModel : GustoViewModel by activityViewModels()

    lateinit var kakaoMap: KakaoMap
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
                            val routeList = gustoViewModel.removeRoute
                            iterateWithDelay(routeList) { routeListId ->
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
    override fun onPause() {
        super.onPause()
        binding.kakaoMap.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.kakaoMap.resume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("viewmodel",gustoViewModel.editMode.toString())
        if(gustoViewModel.editMode) {
            gustoViewModel.editMode = false
            binding.fabEdit.callOnClick()
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


        mapUtil.getCurrentLocation(requireContext(), this, requireActivity()) { location ->
            Log.d(
                "CurrentLocation",
                "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
            )
            binding.kakaoMap.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    Log.e(TAG, "onMapDestroy")
                }

                override fun onMapError(error: Exception?) {
                    Log.e(TAG, "onMApError", error)

                }

            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(getKakaoMap: KakaoMap) {
                    //var cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(37.402005, 127.108621))
                    //kakaoMap.moveCamera(cameraUpdate, CameraAnimation.from(500, true, true))
                    kakaoMap = getKakaoMap
                    kakaoMap.setOnCameraMoveEndListener { kakaoMap, cameraPosition, gestureType ->
                        // 카메라 움직임 종료 시 이벤트 호출
                        // 사용자 제스쳐가 아닌 코드에 의해 카메라가 움직이면 GestureType 은 Unknown
                    }
                    kakaoMap.setOnCameraMoveStartListener { kakaoMap, gestureType ->
                        // 카메라 움직임 시작 시 이벤트 호출
                        // 사용자 제스쳐가 아닌 코드에 의해 카메라가 움직이면 GestureType 은 Unknown
                    }
                    kakaoMap.setOnLabelClickListener { kakaoMap, layer, label ->
                        binding.vpSlider.visibility = View.VISIBLE
                        if (label != null) {
                            Log.d(TAG, label.tag.toString())
                            binding.vpSlider.currentItem = (label.tag as Int) - 1
                        }
                    }
                    kakaoMap.setOnMapClickListener { kakaoMap, position, screenPoint, poi ->
                        binding.vpSlider.visibility = View.GONE
                    }

                    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            // 페이지가 선택되었을 때의 작업 수행
                            if(!itemList.isEmpty()) {
                                var cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(itemList[position].latitude, itemList[position].longitude))
                                kakaoMap.moveCamera(cameraUpdate, CameraAnimation.from(500, true, true))

                            } else {
                                viewPager.visibility = View.GONE
                            }
                        }
                    })
                    gustoViewModel.markerListLiveData.observe(viewLifecycleOwner, Observer { markers ->
                        mapUtil.setRoute(kakaoMap, itemList)
                        adapter.notifyDataSetChanged()
                    })
                    Log.e(TAG, "onMapReady")
                }

                override fun getZoomLevel(): Int {
                    // 지도 시작 시 확대/축소 줌 레벨 설정
                    return 16
                }

                override fun getPosition(): LatLng {
                    return LatLng.from(itemList[0].latitude,itemList[0].longitude)
                }
            })
        }

        if(gustoViewModel.routeStorTmpData != null) {
           var data = gustoViewModel.routeStorTmpData

           if (data != null) {
               gustoViewModel.addRoute.add(data.storeId.toLong())
               gustoViewModel.getStoreDetailQuick(listOf(data.storeId.toLong())) {result, stores ->
                   when(result) {
                       1 -> {
                           if(stores!=null) {
                               for(store in stores) {
                                   gustoViewModel.markerListLiveData.value!!.add(mapUtil.Companion.MarkerItem(
                                       data.storeId.toLong(),
                                       0,
                                       0,
                                       store.latitude,
                                       store.longitude,
                                       data.storeName,
                                       data.address,
                                       false
                                   ))
                               }
                                mapUtil.setRoute(kakaoMap, gustoViewModel.markerListLiveData.value!!)
                            }
                            binding.fabEdit.callOnClick()
                            gustoViewModel.routeStorTmpData = null
                        }
                        else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


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

    val coroutineScope = CoroutineScope(Dispatchers.Main)

    // 포문을 돌 때 0.1초의 딜레이를 주는 함수
    fun iterateWithDelay(routeList: ArrayList<Long>, action: (Long) -> Unit) {
        coroutineScope.launch {
            for (routeListId in routeList) {
                action(routeListId)
                delay(100) // 0.1초의 딜레이를 줍니다.
            }
        }
    }
}