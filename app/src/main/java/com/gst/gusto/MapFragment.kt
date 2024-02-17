package com.gst.clock.Fragment

import MapRecyclerAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gst.gusto.MapMainScreenFragment
import com.gst.gusto.R
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.Util.mapUtil.Companion.setMapInit
import com.gst.gusto.Util.mapUtil.Companion.setMarker
import com.gst.gusto.Util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MapFragment : Fragment(),MapView.POIItemEventListener,MapView.MapViewEventListener {


    lateinit var binding: FragmentMapBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>


    private val TAG = "SOL_LOG"
    lateinit var mapView : MapView
    private val gustoViewModel : GustoViewModel by activityViewModels()

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

        // BottomSheet 상태 변화 감지
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // BottomSheet가 숨겨진 경우 fragment_map_main_screen.xml을 보여줌
                        showMainScreenFragment()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // BottomSheet가 펼쳐진 경우 AreaFragment로 이동
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_map, AreaFragment())
                            .addToBackStack(null) //뒤로가기
                            .commit()
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


        ////    카테고리    ////

        // 버튼 클릭 리스너 설정
        val totalBtn = view.findViewById<Chip>(R.id.total_btn)
        totalBtn.setOnClickListener {
            // 현재 버튼의 텍스트를 가져옴
            val currentText = totalBtn.text.toString()
            // 다음 순서로 변경
            val nextText = when (currentText) {
                "전체" -> "가본 곳 만"
                "가본 곳 만" -> "가본 곳 제외"
                else -> "전체"
            }
            // 변경된 텍스트 설정
            totalBtn.text = nextText
        }


        // 칩 그룹 초기화
        chipGroup = binding.fragmentMapMainScreen.chipGroup

        // 각 칩에 대한 클릭 리스너 설정
        view.findViewById<Chip>(R.id.cafe_btn).setOnClickListener {
            handleChipClick(it as Chip)
        }
        view.findViewById<Chip>(R.id.Italian_btn).setOnClickListener {
            handleChipClick(it as Chip)
        }
        view.findViewById<Chip>(R.id.Japanese_btn).setOnClickListener {
            handleChipClick(it as Chip)
        }
        view.findViewById<Chip>(R.id.Izakaya_btn).setOnClickListener {
            handleChipClick(it as Chip)
        }

        return view
    }
    private fun addChip(text:String) {
        val chip = Chip(requireContext())

        chip.isClickable = true
        chip.isCheckable = true

        chip.text  = text
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_select_color)
        chip.chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.main_C)
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_select_text_color))
        chip.textSize = 15f
        chip.typeface = Typeface.createFromAsset(requireActivity().assets, "font/pretendard_medium.otf")
        chip.chipStrokeWidth = util.dpToPixels(1f, resources.displayMetrics)
        chip.chipCornerRadius = util.dpToPixels(41f, resources.displayMetrics)

        chipGroup.addView(chip)
    }


    // 클릭된 칩의 처리를 담당하는 함수
    private fun handleChipClick(chip: Chip) {
        // 클릭된 칩의 ID
        val clickedChipId = chip.id

        // 클릭된 칩이 이미 활성화된 상태인지 확인
        val isClickedChipActive = previousChipId == clickedChipId

        // 이전에 활성화된 칩이 있으면 해당 칩의 색상을 변경
        if (previousChipId != -1) {
            val previousChip = chipGroup.findViewById<Chip>(previousChipId)
            // 클릭된 칩이 이미 활성화된 상태가 아니거나, 전체 칩이 비활성화된 상태인 경우에만 이전 칩을 비활성화합니다.
            if (!isClickedChipActive || isAllChipsDisabled()) {
                // 이전에 활성화된 칩을 비활성화 상태로 변경
                previousChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_C))
                previousChip.setChipBackgroundColorResource(R.color.chip_select_color)
                previousChip.setChipIconResource(R.drawable.streamline_bean)
                // 이전 칩의 ID를 초기화하여 비활성화 상태로 설정
                previousChipId = -1
            }
        }

        // 클릭된 칩이 이미 활성화된 상태인 경우에만 비활성화
        if (isClickedChipActive) {
            // 클릭된 칩의 색상 변경 (비활성화 상태로 변경)
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_C))
            chip.setChipBackgroundColorResource(R.color.chip_select_color)
            chip.setChipIconResource(R.drawable.streamline_bean)
            // 클릭된 칩의 ID를 초기화하여 비활성화 상태로 설정
            previousChipId = -1
        } else {
            // 클릭된 칩이 이미 활성화된 상태가 아니라면 해당 칩을 활성화
            // 클릭된 칩의 색상 변경
            chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            chip.setChipBackgroundColorResource(R.color.main_C)
            chip.setChipIconResource(R.drawable.streamline_coffee_bean_white)
            // 클릭된 칩의 ID를 이전 칩의 ID로 저장
            previousChipId = clickedChipId
        }
    }

    // 전체 칩이 비활성화되었는지 여부를 확인하는 함수
    private fun isAllChipsDisabled(): Boolean {
        // 모든 칩을 확인하여 비활성화된 칩이 있는지 검사
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.isEnabled) {
                return false
            }
        }
        return true
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

        binding.fragmentArea.apply {
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val layoutManager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val layoutManager3 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            val recyclerView: RecyclerView = recyclerViewNoVisitedRest
            val recyclerView2: RecyclerView = recyclerViewVisitedRest
            val recyclerView3: RecyclerView = recyclerViewAgeNoVisitedRest

            // 아이템 담기
            val itemList = ArrayList<String>()

            // 이미지 리소스 URL
            val imageResource = "https://www.urbanbrush.net/web/wp-content/uploads/edd/2023/02/urban-20230228092421948485.jpg"
            itemList.add(imageResource)
            itemList.add(imageResource)
            itemList.add(imageResource)
            itemList.add(imageResource)
            itemList.add(imageResource)
            itemList.add(imageResource)
            itemList.add(imageResource)
            itemList.add(imageResource)

            val adapter = MapRecyclerAdapter(itemList)
            val adapter2 = MapRecyclerAdapter(itemList)
            val adapter3 = MapRecyclerAdapter(itemList)

            recyclerView.adapter = adapter
            recyclerView2.adapter = adapter2
            recyclerView3.adapter = adapter3

            // 레이아웃 매니저 설정
            recyclerView.layoutManager = layoutManager
            recyclerView2.layoutManager = layoutManager2
            recyclerView3.layoutManager = layoutManager3

            // 스크롤바 숨기기
            recyclerView.isVerticalScrollBarEnabled = false
            recyclerView2.isVerticalScrollBarEnabled = false
            recyclerView3.isVerticalScrollBarEnabled = false
        }

    }
    private fun showMainScreenFragment() {
        // fragment_map_main_screen.xml을 보이게 하는 작업

        binding.listViewBtn.visibility

        val mainScreenFragment = MapMainScreenFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_map, mainScreenFragment)
            .commit()
    }

    private fun hideMainScreenFragment() {
        // fragment_map_main_screen.xml을 숨기는 작업

        binding.listViewBtn.isGone

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

        mapView = MapView(requireContext())

        mapView.setPOIItemEventListener(this)
        mapView.setMapViewEventListener(this)

        setMapInit(mapView,binding.kakaoMap, requireContext(),requireActivity(),"map")

        setMarker(mapView,markerList)
    }
    override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        // 마커 클릭 시 이벤트
        Log.d("MapViewEventListener","ccc")
        findNavController().navigate(R.id.action_fragment_map_to_mapViewpagerFragment)
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
        if (p1 != null) {
            gustoViewModel.getRegionInfo(p1.mapPointGeoCoord.longitude, p1.mapPointGeoCoord.latitude)  {result ->
                when(result) {
                    1 -> {
                        Log.d("viewmodel",gustoViewModel.dong)
                    }
                }
            }
        }
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도 이동이 완료되었습니다.")
    }



}


