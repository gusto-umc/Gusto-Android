package com.gst.gusto


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.util.mapUtil
import com.gst.gusto.util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMapViewpagerBinding
import com.gst.gusto.list.adapter.RouteViewPagerAdapter
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory

class MapViewpagerFragment : Fragment(){

    lateinit var binding: FragmentMapViewpagerBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    val markerList = ArrayList<mapUtil.Companion.MarkerItem>()
    lateinit var kakaoMap: KakaoMap
    private val TAG = "MapViewpagerFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapViewpagerBinding.inflate(inflater, container, false)

        // 클릭 리스너 설정
        binding.listViewBtn.setOnClickListener {
            // 버튼이 클릭되었을 때 실행될 동작
            gustoViewModel.keepFlag = true
            findNavController().popBackStack()
        }
        getMarker()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * 검색창 클릭 리스너
         */
        binding.searchShort.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_viewpager_to_searchFragment)
        }
        binding.tvMapSearchShort.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_viewpager_to_searchFragment)
        }
        binding.ivMapSearchboxShort.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_viewpager_to_searchFragment)
        }
        binding.reviewAddBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_viewpager_to_searchFragment)
        }


    }
    fun getMarker() {
        var num = 2
        gustoViewModel.getStoreDetailQuick(gustoViewModel.storeIdList) {result, stores ->
            when(result) {
                1 -> {
                    if (stores != null) {
                        for(data in stores) {
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
                                markerList.add(0,tmpData)
                            } else markerList.add(tmpData)
                            Log.d("viewmodel","${tmpData.toString()}, ${markerList.size+2}, ${gustoViewModel.storeIdList.size-1}")
                        }
                    }
                    if (markerList.size == gustoViewModel.storeIdList.size) {
                        Log.d("viewmodel","set marker!!!")
                    }
                    initMap()

                }
                else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun initMap() {
        val viewPager = binding.vpSlider

        // 이미지 슬라이드
        val adapter = RouteViewPagerAdapter(markerList,requireActivity() as MainActivity,1)
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
                    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            // 페이지가 선택되었을 때의 작업 수행
                            if(!markerList.isEmpty()) {
                                var cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(markerList[position].latitude, markerList[position].longitude))

                                kakaoMap.moveCamera(cameraUpdate, CameraAnimation.from(500, true, true))
                            } else {
                                viewPager.visibility = View.GONE
                            }
                        }
                    })
                    mapUtil.setMarker(kakaoMap, markerList)
                    Log.e(TAG, "onMapReady")
                }

                override fun getZoomLevel(): Int {
                    // 지도 시작 시 확대/축소 줌 레벨 설정
                    return 16
                }

                override fun getPosition(): LatLng {
                    return LatLng.from(markerList[0].latitude,markerList[0].longitude)
                }
            })
        }


    }

    override fun onPause() {
        super.onPause()
        binding.kakaoMap.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.kakaoMap.resume()
    }
}
