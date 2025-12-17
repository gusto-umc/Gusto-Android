package com.gst.clock.Fragment


import MapRecyclerAdapter
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gst.gusto.BuildConfig
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseMapCategory
import com.gst.gusto.api.ResponseSavedStoreData
import com.gst.gusto.databinding.FragmentMapBinding
import com.gst.gusto.list.adapter.RouteViewPagerAdapter
import com.gst.gusto.util.mapUtil
import com.gst.gusto.util.mapUtil.Companion.MarkerItem
import com.gst.gusto.util.mapUtil.Companion.getCurrentLocation
import com.gst.gusto.util.mapUtil.Companion.setMarker
import com.gst.gusto.util.util
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory


class MapFragment : Fragment() {


    lateinit var binding: FragmentMapBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val gustoViewModel : GustoViewModel by activityViewModels()
    val markerList = ArrayList<MarkerItem>()
    private var isVisited:Boolean? = null
    lateinit var chipGroup: ChipGroup
    private var currentChips = ArrayList<Int>()
    // 이전에 활성화된 칩을 저장하는 변수
    private var previousChipId: Int = -1
    lateinit var kakaoMap: KakaoMap

    private var curLatitude = 0.0
    private var curLongitude = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        val totalBtn = view.findViewById<Chip>(R.id.total_btn)
        totalBtn.setOnClickListener {
            // 현재 버튼의 텍스트를 가져옴
            val currentText = totalBtn.text.toString()
            //다음 순서로 변경
            val nextText = when (currentText) {
                "전체" -> {
                    isVisited = true
                    "가본 곳 만"
                }
                "가본 곳 만" -> {
                    isVisited = false
                    "가본 곳 제외"
                }
                else -> {
                    isVisited = null
                    "전체"
                }
            }

            reGetMapMarkers()
            totalBtn.text = nextText
        }

        // 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도
        if (!mapUtil.hasPermission(requireContext())) {
            requestPermissions(
                mapUtil.MAPPERMISSIONS,
                mapUtil.LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            initMap()
        }

        chipGroup = binding.fragmentMapMainScreen.chipGroup

        return view
    }

    private fun loadCategories(townName: String) {
        Log.d("dsadsasda", townName)
        gustoViewModel.getMapCategory(townName) { resultCode ->
            when (resultCode) {
                0 -> {
                    // 성공적으로 데이터를 가져왔을 때
                    val categories = gustoViewModel.myMapCategoryList
                    populateChips(categories)
                }
            }
        }
    }
    private fun populateChips(categories: ArrayList<ResponseMapCategory>?) {
        val nonNullCategories = categories ?: return // null일 경우 함수 종료
        chipGroup.removeAllViews() // 기존 칩 제거 (재로드하는 경우 필요)
        for ((index, category) in nonNullCategories.withIndex()) {
            addChip(
                text = category.categoryName,
                chipId = category.myCategoryId,
                chipIndex = index,
                categoryIcon = category.categoryIcon
            )
        }
    }

    private fun addChip(text: String, chipId: Int, chipIndex: Int, categoryIcon: Int) {
        val chip = Chip(requireContext())

        chip.id = chipId // 고유한 ID 할당
        chip.isClickable = true
        chip.isCheckable = true

        chip.text = text
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_select_color)
        chip.chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.main_C)
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_select_text_color))
        chip.textSize = 15f
        chip.typeface = Typeface.createFromAsset(requireActivity().assets, "font/pretendard_medium.otf")
        chip.chipStrokeWidth = util.dpToPixels(1f, resources.displayMetrics)
        chip.chipCornerRadius = util.dpToPixels(41f, resources.displayMetrics)
        chip.setChipIconTintResource(R.color.main_C)
        chip.setChipIconResource(gustoViewModel.findIconResource(categoryIcon))

        //칩그룹에 대한 클릭리스너
        chip.setOnClickListener {
            handleChipClick(chip)
        }
        chipGroup.addView(chip, chipIndex)
    }

    // 클릭된 칩의 처리를 담당하는 함수
    private fun handleChipClick(chip: Chip) {
        val clickedChipId = chip.id

        val isClickedChipActive = !chip.isChecked

        if (isClickedChipActive) {
            chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_disabled))
            chip.setChipBackgroundColorResource(R.color.white)
            chip.setChipIconTintResource(R.color.main_C)
            currentChips.remove(clickedChipId)
        } else {
            chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            chip.setChipBackgroundColorResource(R.color.main_C)
            chip.setChipIconTintResource(R.color.white)
            currentChips.add(clickedChipId)
        }

        reGetMapMarkers()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gustoViewModel.changeDong("")
        //목록 보기 클릭 리스너 - 민디
        binding.listViewBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_categoryFragment)
        }

        // 카테고리 선택 초기화
        currentChips.clear()


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
        binding.reviewAddBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_reviewAddSearch)
        }

        val knownStore = binding.fragmentArea.knownStore
        val newStore = binding.fragmentArea.newStore

        knownStore.isSelected = true
        updateButtonStyle()

        knownStore.setOnClickListener {
            if (!it.isSelected) {
                knownStore.isSelected = true
                newStore.isSelected = false
                updateButtonStyle()
            }
        }

        newStore.setOnClickListener {
            if (!it.isSelected) {
                newStore.isSelected = true
                knownStore.isSelected = false
                updateButtonStyle()
            }
        }
      

        // 드래그 리스너 설정
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // 상태 변경 시 호출됩니다.
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.listViewBtn.visibility = View.VISIBLE
                        binding.reviewAddBtn.visibility = View.VISIBLE
                        // 바텀 시트가 축소된 상태입니다.
                        // 원하는 동작을 수행하세요.
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // 바텀 시트가 확장된 상태입니다.
                        // 원하는 동작을 수행하세요.
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        binding.listViewBtn.visibility = View.GONE
                        binding.reviewAddBtn.visibility = View.GONE
                        // 바텀 시트가 드래그 중인 상태입니다.
                        // 원하는 동작을 수행하세요.
                    }
                    // 다른 상태에 대한 처리도 필요하다면 추가하세요.
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 드래그 중일 때 호출됩니다.
                // slideOffset은 -1(바텀 시트 완전히 닫힘)부터 1(바텀 시트 완전히 열림)까지의 값입니다.
                // 원하는 동작을 수행하세요.
                binding.vpSlider.visibility = View.GONE
            }
        })

    }
    private fun updateButtonStyle() {
        // "아는 가게에요!" 버튼 스타일
        binding.fragmentArea.knownStore.apply {
            if (isSelected) {
                setTextColor(ContextCompat.getColor(context, R.color.main_C))
                background = ContextCompat.getDrawable(context, R.drawable.background_radius_20_stroke_2_fill_white)
                binding.fragmentArea.recyclerViewVisitedRest.visibility = View.VISIBLE
                binding.fragmentArea.recyclerViewNoVisitedRest.visibility = View.GONE
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.gray_3))
                background.alpha = 0
            }
        }

        // "NEW PLACE" 버튼 스타일
        binding.fragmentArea.newStore.apply {
            if (isSelected) {
                setTextColor(ContextCompat.getColor(context, R.color.main_C))
                background = ContextCompat.getDrawable(context, R.drawable.background_radius_20_stroke_2_fill_white)
                binding.fragmentArea.recyclerViewVisitedRest.visibility = View.GONE
                binding.fragmentArea.recyclerViewNoVisitedRest.visibility = View.VISIBLE
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.gray_3))
                background = ContextCompat.getDrawable(context, R.drawable.background_radius_20)
                background.alpha = 0
            }
        }
    }
    override fun onResume() {
        super.onResume()
        binding.kakaoMap.resume()

    }



    override fun onPause() {
        binding.kakaoMap.removeAllViews()
        super.onPause()
        binding.kakaoMap.pause()
    }

    //현재 동을 불러오기//
    //현재 동에 대한 작업 불러오기//
    fun refindDong(){
        //사진 불러와서 리사이클러뷰와 연결해 담기//
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val layoutManager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val recyclerView: RecyclerView = binding.fragmentArea.recyclerViewNoVisitedRest
        val recyclerView2: RecyclerView = binding.fragmentArea.recyclerViewVisitedRest

        // 아이템 담기
        val itemList_unvisit = ArrayList<ResponseSavedStoreData>()
        val itemList_visit = ArrayList<ResponseSavedStoreData>()

        // 방문 X - 각 가게에 대한 정보
        gustoViewModel.mapUnvisitedList?.let { unvisitedStores ->
            for (store in unvisitedStores) {
                Log.d("log_img",store.toString())
                itemList_unvisit.add(store)
            }
        }

        // 방문 O - 각 가게에 대한 정보
        gustoViewModel.mapVisitedList?.let { visitedStores ->
            for (store in visitedStores) {
                Log.d("log_img",store.toString())
                itemList_visit.add(store)
            }
        }

        val adapter = MapRecyclerAdapter(itemList_unvisit,requireActivity() as MainActivity)
        val adapter2 = MapRecyclerAdapter(itemList_visit, requireActivity() as MainActivity)
        recyclerView.adapter = adapter
        recyclerView2.adapter = adapter2

        // 레이아웃 매니저 설정
        recyclerView.layoutManager = layoutManager
        recyclerView2.layoutManager = layoutManager2

        // 스크롤바 숨기기
        recyclerView.isVerticalScrollBarEnabled = false
        recyclerView2.isVerticalScrollBarEnabled = false


    }
    fun initMap() {
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

        getCurrentLocation(requireContext(), this, requireActivity()) { location ->
            Log.d("CurrentLocation", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
            binding.kakaoMap.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                }

                override fun onMapError(error: Exception?) {

                }

                override fun onMapResumed() {
                    super.onMapResumed()

                }

            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(getKakaoMap: KakaoMap) {
                    kakaoMap = getKakaoMap

                    kakaoMap.setOnCameraMoveEndListener { kakaoMap, cameraPosition, gestureType ->
                        // 카메라 움직임 종료 시 이벤트 호출
                        // 사용자 제스쳐가 아닌 코드에 의해 카메라가 움직이면 GestureType 은 Unknown
                        Log.e("ERR", "cur loc : "+cameraPosition.toString())
                        gustoViewModel.getNewRegionInfo(cameraPosition.position.longitude, cameraPosition.position.latitude,
                            BuildConfig.SGIS_CONSUMER_KEY,  BuildConfig.SGIS_CONSUMER_SECRET) { result, address ->
                            when(result) {
                                1 -> {
                                    curLatitude = cameraPosition.position.latitude
                                    curLongitude = cameraPosition.position.longitude

                                    binding.fragmentArea.userLocation.text = address
                                    loadCategories(gustoViewModel.dong.value!!)
                                    gustoViewModel.getSavedStores(gustoViewModel.dong.value!!, null){
                                            result ->
                                        when(result){
                                            0 -> {
                                                refindDong()
                                            }
                                            1 -> {
                                                Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }

                                    reGetMapMarkers()
                                }
                            }
                        }
                    }
                    kakaoMap.setOnMapClickListener { kakaoMap, position, screenPoint, poi ->
                        binding.vpSlider.visibility = View.GONE
                    }
                    kakaoMap.setOnCameraMoveStartListener { kakaoMap, gestureType ->
                        // 카메라 움직임 시작 시 이벤트 호출
                        // 사용자 제스쳐가 아닌 코드에 의해 카메라가 움직이면 GestureType 은 Unknown
                    }
                    kakaoMap.setOnLabelClickListener { kakaoMap, layer, label ->
                        binding.vpSlider.visibility = View.VISIBLE

                        if (label != null) {
                            binding.vpSlider.postDelayed({
                                binding.vpSlider.currentItem = (label.tag as Int) - 1
                            }, 100) // 0.1초 = 100ms
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

                    gustoViewModel.getNewRegionInfo(position.longitude, position.latitude,
                        BuildConfig.SGIS_CONSUMER_KEY,  BuildConfig.SGIS_CONSUMER_SECRET) { result, address ->
                        when(result) {
                            1 -> {
                                curLatitude = position.latitude
                                curLongitude = position.longitude

                                gustoViewModel.userLongtitude = position.longitude
                                gustoViewModel.userLatitude = position.latitude


                                binding.fragmentArea.userLocation.text = address
                                loadCategories(gustoViewModel.dong.value!!)
                                gustoViewModel.getSavedStores(gustoViewModel.dong.value!!, null){
                                        result ->
                                    when(result){
                                        0 -> {
                                            refindDong()
                                        }
                                        1 -> {
                                            Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                                reGetMapMarkers()
                            }
                        }
                    }
                }
                override fun getZoomLevel(): Int {
                    // 지도 시작 시 확대/축소 줌 레벨 설정
                    return 16
                }
                override fun getPosition(): LatLng {
                    return LatLng.from(location.latitude, location.longitude)
                }
            })
        }

    }
    fun reGetMapMarkers() {
        gustoViewModel.getCurrentMapStores(curLongitude,curLatitude,currentChips.toMutableList(),isVisited) {result, datas ->
            when(result) {
                1 -> {
                    markerList.clear()
                    if(datas!=null) {
                        for((index,data) in datas.withIndex()) {
                            markerList.add(MarkerItem(data.storeId, index+1,0, data.latitude!!, data.longitude!!, data.storeName!!, "", "",true))
                        }
                    }
                    binding.vpSlider.adapter?.notifyDataSetChanged()

                    setMarker(kakaoMap,markerList)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == mapUtil.LOCATION_PERMISSION_REQUEST_CODE) {
            // 권한 요청 코드가 일치하는 경우
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap()
            } else {
                // 사용자가 권한을 거부한 경우 또는 권한이 부여되지 않은 경우
                // 필요한 조치를 취하십시오. 예를 들어, 사용자에게 권한이 필요한 이유를 설명하는 다이얼로그를 표시하거나 기능을 비활성화할 수 있습니다.
            }
        }
    }

}


