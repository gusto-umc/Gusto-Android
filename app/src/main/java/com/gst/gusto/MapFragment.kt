package com.gst.clock.Fragment

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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gst.gusto.MapMainScreenFragment
import com.gst.gusto.R
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MapFragment : Fragment(),MapView.POIItemEventListener,MapView.MapViewEventListener {


    lateinit var binding: FragmentMapBinding
    private val TAG = "SOL_LOG"
    lateinit var mapView : MapView

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000

    //private lateinit var naverMap: NaverMap
    //private lateinit var locationSource: FusedLocationSource

    lateinit var  chipGroup: ChipGroup

    // 이전에 활성화된 칩을 저장하는 변수
    private var previousChipId: Int = -1

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

        chipGroup = binding.fragmentMapMainScreen.chipGroup



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


        // 버튼 클릭 리스너 설정
        binding.listViewBtn.setOnClickListener {
            // 네비게이션 컨트롤러를 사용하여 다른 프래그먼트로 이동
            //navController.navigate(R.id.listFragment)
        }

        // 칩그룹에 칩 클릭 리스너 추가
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            // 이전에 활성화된 칩이 있으면 해당 칩의 색상을 변경
            if (previousChipId != -1) {
                val previousChip = group.findViewById<Chip>(previousChipId)
                previousChip.setChipBackgroundColorResource(R.color.white)
            }
            // 현재 클릭된 칩의 색상 변경
            val currentChip = group.findViewById<Chip>(checkedId)
            currentChip.setChipBackgroundColorResource(R.color.chip_select_color)
            // 클릭된 칩의 ID를 이전 칩의 ID로 저장
            previousChipId = checkedId
        }

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //목록 보기 클릭 리스너 - 민디
        binding.listViewBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewFragment)
        }
        /*
        //방문 o 클릭 리스너 -> 보완 예정
        binding.fragmentArea.vis1.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewSaveFragment2)
        }
        //방문 x 클릭 리스너 -> 보완 예정
        binding.fragmentArea.vis01.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewSaveFragment2)
        }
         */
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

    override fun onResume() {
        super.onResume()

        val markerList = ArrayList<MarkerItem>()
        markerList.add(MarkerItem(0, 0,0, 37.6215101, 127.0751410, "", "", false))
        markerList.add(MarkerItem(0, 0,0, 37.6245301, 127.0740210, "", "", false))
        markerList.add(MarkerItem(0, 0,0, 37.6215001, 127.0743010, "", "", false))
/*
        mapView = MapView(requireContext())

        mapView.setPOIItemEventListener(this)
        mapView.setMapViewEventListener(this)

        setMapInit(mapView,binding.kakaoMap, requireContext(),requireActivity(),"map")

        setMarker(mapView,markerList)*/
    }
    override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        // 마커 클릭 시 이벤트
        Log.d("MapViewEventListener","ccc")

    }
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {}
    override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {}

    override fun onPause() {
        super.onPause()
        Log.d("MapViewEventListener","onPause")
        binding.kakaoMap.removeAllViews()
    }

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
        Log.d(TAG, "지도 드래그가 시작되었습니다.")
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도 드래그가 종료되었습니다.")
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도 이동이 완료되었습니다.")
    }



}


