package com.gst.clock.Fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gst.gusto.MainActivity
import com.gst.gusto.MapMainScreenFragment
import com.gst.gusto.R
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.Util.mapUtil.Companion.setMapInit
import com.gst.gusto.Util.mapUtil.Companion.setMarker
import com.gst.gusto.Util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMapBinding
import com.gst.gusto.list.adapter.RouteViewPagerAdapter
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import okhttp3.internal.notify


class MapFragment : Fragment(),MapView.POIItemEventListener,MapView.MapViewEventListener {


    lateinit var binding: FragmentMapBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>


    private val TAG = "SOL_LOG"
    lateinit var mapView : MapView
    private val gustoViewModel : GustoViewModel by activityViewModels()

    val markerList = ArrayList<MarkerItem>()

    lateinit var  chipGroup: ChipGroup

    // 이전에 활성화된 칩을 저장하는 변수
    private var previousChipId: Int = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        // BottomSheet 설정
        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)


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

    // 클릭된 칩의 처리를 담당하는 함수
    private fun handleChipClick(chip: Chip) {
        // 이전에 활성화된 칩이 있으면 해당 칩의 색상을 변경
        if (previousChipId != -1) {
            val previousChip = chipGroup.findViewById<Chip>(previousChipId)
            // 이전에 활성화된 칩
            previousChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_C))
            previousChip.setChipBackgroundColorResource(R.color.chip_select_color)
            previousChip.setChipIconResource(R.drawable.streamline_bean)
        }
        // 현재 클릭된 칩의 색상 변경
        chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        chip.setChipBackgroundColorResource(R.color.main_C)
        chip.setChipIconResource(R.drawable.streamline_coffee_bean_white)
        // 클릭된 칩의 ID를 이전 칩의 ID로 저장
        previousChipId = chip.id
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //목록 보기 클릭 리스너 - 민디
        binding.listViewBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewFragment)
        }

        /**
         * 방문 o 클릭 리스너 -> 보완 예정
         */
        binding.fragmentArea.firstVisit.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewSaveFragment2)
        }
        /**
         * 방문 x 클릭 리스너 -> 보완 예정
         */
        binding.fragmentArea.prevVisited.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewSaveFragment2)
        }

        /**
         * 검색화면 클릭 리스너 - mindy
         */
        binding.fragmentMapMainScreen.search.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_searchFragment)
        }
        binding.fragmentMapMainScreen.ivMapSearchbox.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_searchFragment)
        }
        binding.fragmentMapMainScreen.tvMapSearch.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_searchFragment)
        }

        /**
         * 카테고리 전체 조회 - mindy
         */
        // 데이터 넣어둔 변수 : gustoViewModel.myMapCategoryList
        gustoViewModel.getMapCategory("성수1가1동"){
            result ->
            when(result){
                0 -> {
                    //success
                }
                1 -> {
                    //fail
                    Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        /**
         * 저장된 맛집 조회 - mindy
         * 현재 카테고리 선택이 구현 보류로 categoryId에 null 넣고 추후 보완 예정
         */
        //리스트 별로 저장
        // 방문X 리스트 저장 변수 : gustoViewModel.mapUnvisitedList
        // 방문X 개수 : gustoViewModel.mapUnvisitedCnt
        // 방문 O 리스트 저장 변수 : gustoViewModel.mapVisitedList
        // 방문o 개수 : gustoViewModel.mapVisitedCnt
        //닉네임 변수 : gustoViewModel.userNickname
        gustoViewModel.getSavedStores("성수1가1동", null){
            result ->
            when(result){
                0 -> {}
                1 -> {
                    Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val viewPager = binding.vpSlider

        // 이미지 슬라이드
        val adapter = RouteViewPagerAdapter(markerList,requireActivity() as MainActivity,2)
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
                if(!markerList.isEmpty()) {
                    val mapPoint = MapPoint.mapPointWithGeoCoord(markerList[position].latitude, markerList[position].longitude)
                    mapView.moveCamera(CameraUpdateFactory.newMapPoint(mapPoint,mapView.zoomLevelFloat))
                } else {
                    viewPager.visibility = View.GONE
                }
            }
        })

    }
    /*
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
*/
    override fun onResume() {
        super.onResume()
        mapView = MapView(requireContext())

        mapView.setPOIItemEventListener(this)
        mapView.setMapViewEventListener(this)

        setMapInit(mapView,binding.kakaoMap, requireContext(),requireActivity(),"map",this)
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


    override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        // 마커 클릭 시 이벤트
        Log.d("MapViewEventListener","ccc")
        binding.vpSlider.visibility = View.VISIBLE
        if (poiItem != null) {
            binding.vpSlider.currentItem = poiItem.itemName.toInt()-1
        }
        //findNavController().navigate(R.id.action_fragment_map_to_mapViewpagerFragment)
    }
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {}
    override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {}

    override fun onPause() {
        binding.kakaoMap.removeAllViews()
        super.onPause()
        Log.d("MapViewEventListener","onPause")
        Log.e("viewmodel","DIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIEDIE")
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
        binding.vpSlider.visibility = View.GONE
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "지도 드래그가 종료되었습니다.")

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        if (p1 != null) {
            gustoViewModel.getRegionInfo(p1.mapPointGeoCoord.longitude, p1.mapPointGeoCoord.latitude)  {result, address ->
                when(result) {
                    1 -> {
                        Log.d("viewmodel",gustoViewModel.dong)
                        if(binding.fragmentArea.userLoc.text =="현재 사용자의 위치")
                            binding.fragmentArea.userLoc.text = address
                        gustoViewModel.getCurrentMapStores {result, datas ->
                            when(result) {
                                1 -> {
                                    markerList.clear()
                                    if(datas!=null) {
                                        for((index,data) in datas.withIndex()) {
                                            markerList.add(MarkerItem(data.storeId, index+1,0, data.latitude!!, data.longitude!!, data.storeName!!, "", false))
                                        }
                                    }
                                    Log.d("viewmodel","${markerList}")
                                    setMarker(mapView,markerList)
                                    binding.vpSlider.adapter?.notifyDataSetChanged()
                                }
                                else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == mapUtil.LOCATION_PERMISSION_REQUEST_CODE) {
            // 권한 요청 코드가 일치하는 경우
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setMapInit(mapView,binding.kakaoMap, requireContext(),requireActivity(),"map",this)
            } else {
                // 사용자가 권한을 거부한 경우 또는 권한이 부여되지 않은 경우
                // 필요한 조치를 취하십시오. 예를 들어, 사용자에게 권한이 필요한 이유를 설명하는 다이얼로그를 표시하거나 기능을 비활성화할 수 있습니다.
            }
        }
    }

}


