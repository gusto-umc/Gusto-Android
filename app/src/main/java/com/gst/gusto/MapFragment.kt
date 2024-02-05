package com.gst.clock.Fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gst.gusto.MapMainScreenFragment
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : Fragment(), OnMapReadyCallback, NaverMap.OnMapClickListener {

    lateinit var binding: FragmentMapBinding

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            //NaverMapSdk.getInstance(it).client =
            //    NaverMapSdk.NaverCloudPlatformClient("3yu23i1pd1")
        }

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            initMapView()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        // BottomSheet 설정
        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)


        // BottomSheet 상태 변화 감지
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // BottomSheet가 숨겨진 경우 fragment_map_main_screen.xml을 보여줌
                        showMainScreenFragment()
                    }
                    else -> {
                        // 다른 상태에서는 fragment_map_main_screen.xml을 숨김
                        hideMainScreenFragment()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이딩 중일 때 추가 작업이 필요하면 여기에 추가
            }
        })
        return view
    }

    private fun showMainScreenFragment() {
        // fragment_map_main_screen.xml을 보이게 하는 작업
        val mainScreenFragment = MapMainScreenFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_map, mainScreenFragment)
            .commit()
    }

    private fun hideMainScreenFragment() {
        // fragment_map_main_screen.xml을 숨기는 작업
        val mainScreenFragment =
            childFragmentManager.findFragmentById(R.id.fragment_map) as? MapMainScreenFragment
        mainScreenFragment?.let {
            childFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    private fun initMapView() {
        /*
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

         */
    }

    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        naverMap.locationSource = locationSource //위치정보제공
        naverMap.uiSettings.isLocationButtonEnabled = true //현재위치 나타내기
        //naverMap.locationTrackingMode = LocationTrackingMode.Follow //위치트래킹

        // 위치 소스 상태 확인
        when (locationSource.isActivated) {
            true -> println("LocationSource Activated")
            false -> println("LocationSource Not Activated")
        }

        // 지도 클릭 리스너 설정
        naverMap.setOnMapClickListener(this)

        // 초기 위치에 마커 추가
        val initialPosition = LatLng(37.5665, 126.9780)
        val marker = Marker()
        marker.position = initialPosition
        marker.map = naverMap

        //MapMainScreenFragment 띄우기
        val mainScreenFragment = MapMainScreenFragment()

        // Use activity's fragmentManager instead of childFragmentManager
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_map, mainScreenFragment)
            .commit()
    }


    fun onMapClick(point: LatLng, coord: Point) {
        // 클릭한 위치에 마커 추가
        val marker = Marker()
        marker.position = point
        marker.map = naverMap

        // 마커 클릭 리스너 설정
        marker.setOnClickListener {
            Toast.makeText(requireContext(), "Marker Clicked!", Toast.LENGTH_SHORT).show()
            true
        }

        // 마커를 클릭할 때 카메라 이동
        val cameraUpdate = CameraUpdate.scrollTo(point)
        naverMap.moveCamera(cameraUpdate)
    }

    override fun onMapClick(p0: PointF, p1: LatLng) {
        TODO("Not yet implemented")
    }
}

