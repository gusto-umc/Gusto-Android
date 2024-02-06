package com.gst.clock.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gst.gusto.MapMainScreenFragment
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMapBinding
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.setMapTilePersistentCacheEnabled


class MapFragment : Fragment(){

    data class MarkerItem (val id : String, val latitude : Double, val longitude : Double)
    lateinit var binding: FragmentMapBinding
    lateinit var mapView : MapView

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
        val markerList = ArrayList<MarkerItem>()
        markerList.add(MarkerItem("0",37.53737528, 127.00557635))

        //setMapInit()
        //setMarker(markerList)
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //목록 보기 클릭 리스너 - 민디
        binding.listViewBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewFragment)
        }

        //방문 o 클릭 리스너 -> 보완 예정
        binding.fragmentArea.vis1.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewSaveFragment2)
        }
        //방문 x 클릭 리스너 -> 보완 예정
        binding.fragmentArea.vis01.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewSaveFragment2)
        }
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
    @SuppressLint("MissingPermission")
    private fun setMapInit() {
        mapView = MapView(requireContext())
        binding.kakaoMap.addView(mapView)
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())



            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { success: Location? ->
                    success?.let { location ->
                        val geocoder = Geocoder(requireContext())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (addresses != null) {
                            val address = addresses[0]
                            val currentAddress = address.getAddressLine(0) // 필요에 따라 세부 정보를 더 가져올 수 있습니다

                            // currentAddress를 필요에 따라 사용하세요
                            Log.d("현재 주소: ", "$currentAddress")
                            //mapView.setCurrentLocationEventListener()
                            setMapTilePersistentCacheEnabled(true)  //다운로드한 지도를 캐시에 저장
                            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude), true)
                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving)
                            mapView.setShowCurrentLocationMarker(true)
                            mapView.setCurrentLocationRadius(10)
                            mapView.setCurrentLocationRadiusStrokeColor(Color.BLUE)
                        }
                    }
                }
                .addOnFailureListener { fail ->
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true)
                }
        }
    }
    private fun setMarker(markerList: ArrayList<MarkerItem>) {
        mapView.removeAllPOIItems()
        for(data in markerList) {
            val marker = MapPOIItem()
            marker.itemName = "Default Marker"
            marker.tag = 0 // id
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude)
            marker.markerType = MapPOIItem.MarkerType.CustomImage
            marker.customImageResourceId = R.drawable.marker_color_small_img
            marker.isShowCalloutBalloonOnTouch = false

            mapView.addPOIItem(marker)
        }
        mapView.setPOIItemEventListener(MarkerEventListener(requireContext()))
    }

    class MarkerEventListener(val context: Context): MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            // 마커 클릭 시 이벤트
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
            // 말풍선 클릭 시 (Deprecated)
            // 이 함수도 작동하지만 그냥 아래 있는 함수에 작성하자
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
            // 말풍선 클릭 시
        }

        override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
            // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
        }
    }
}

