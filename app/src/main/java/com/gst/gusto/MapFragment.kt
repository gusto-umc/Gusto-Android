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
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
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

    private val TAG = "SOL_LOG"
    private val gustoViewModel : GustoViewModel by activityViewModels()

    val markerList = ArrayList<MarkerItem>()

    private var isVisited = false

    lateinit var chipGroup: ChipGroup
    private var currentChip:Int?=null

    // 이전에 활성화된 칩을 저장하는 변수
    private var previousChipId: Int = -1

    lateinit var kakaoMap: KakaoMap

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
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        ////    카테고리    ////

        // 버튼 클릭 리스너 설정
        val totalBtn = view.findViewById<Chip>(R.id.total_btn)
        totalBtn.setOnClickListener {
            // 현재 버튼의 텍스트를 가져옴
            val currentText = totalBtn.text.toString()
            //다음 순서로 변경
            val nextText = when (currentText) {
                "전체" -> "가본 곳 만"
                "가본 곳 만" -> "가본 곳 제외"
                else -> "전체"
            }

            // 지도 설정
            if(isVisited) isVisited = false
            else isVisited = true
            reGetMapMarkers()

            // 변경된 텍스트 설정
            totalBtn.text = nextText
        }

        // 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도 카카오 지도
        if (!mapUtil.hasPermission(requireContext())) {
            requestPermissions(
                mapUtil.MAPPERMISSIONS,
                mapUtil.LOCATION_PERMISSION_REQUEST_CODE
            )
        } else initMap()


        // 칩 그룹 초기화
        chipGroup = binding.fragmentMapMainScreen.chipGroup


        return view
    }


// 카테고리 조회 및 칩 추가
    fun getMapCategoryAndAddChips(townName: String) {
        gustoViewModel.getMapCategory(townName) { result ->
            if (result == 0) {
                // 카테고리 목록을 성공적으로 가져왔을 때
                val categoryList = gustoViewModel.myMapCategoryList
                if (categoryList != null) {
                    for ((index, category) in categoryList.withIndex()) {
                        addChip(category.categoryName, category.myCategoryId, index,category.categoryIcon)
                        Log.d("chip","칩 불러오기")
                    }
                } else {
                    Log.e("getMapCategoryAndAddChips", "Category list is null")
                }
            } else {
                // 카테고리 목록을 가져오지 못했을 때
                Log.e("getMapCategoryAndAddChips", "Failed to get category list")
            }
        }
    }

    // 칩 추가
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

        Log.d("chip","칩 생성")

        //칩그룹에 대한 클릭리스너
        chip.setOnClickListener {
            handleChipClick(chip)
            Log.d("chip", "$chipId")
        }
        chipGroup.addView(chip, chipIndex)
    }

    // 클릭된 칩의 처리를 담당하는 함수
    private fun handleChipClick(chip: Chip) {
        Log.d("chip", "칩 클릭 이벤트 발생")

        // 클릭된 칩의 ID
        val clickedChipId = chip.id

        // 클릭된 칩이 이미 활성화된 상태인지 확인
        val isClickedChipActive = previousChipId == clickedChipId

        // 다른 칩이 활성화된 상태인 경우 이전 칩을 비활성화
        if (!isClickedChipActive && previousChipId != -1) {
            Log.d("chip","이전 칩 비활성화 ${previousChipId}")
            val previousChip = chipGroup.findViewById<Chip>(previousChipId)
            previousChip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_disabled))
            previousChip.setChipBackgroundColorResource(R.color.white)
            previousChip.setChipIconTintResource(R.color.main_C)
        }

        // 클릭된 칩이 이미 활성화된 상태라면 비활성화
        if (isClickedChipActive) {
            // 클릭된 칩의 색상 변경 (비활성화 상태로 변경)
            Log.d("chip", "클릭된 칩 비활성화 ${chip.id}")
            chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_disabled))
            chip.setChipBackgroundColorResource(R.color.white)
            chip.setChipIconTintResource(R.color.main_C)
            // 클릭된 칩의 ID를 초기화하여 비활성화 상태로 설정
            previousChipId = -1
            currentChip = null
        } else {
            // 클릭된 칩을 활성화
            Log.d("chip", "활성화 ${chip.id}")
            chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            chip.setChipBackgroundColorResource(R.color.main_C)
            chip.setChipIconTintResource(R.color.white)
            // 클릭된 칩의 ID를 이전 칩의 ID로 저장
            previousChipId = clickedChipId
            currentChip = clickedChipId
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
        gustoViewModel.changeDong("")
        //목록 보기 클릭 리스너 - 민디
        binding.listViewBtn.setOnClickListener {
            //Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_mapListViewFragment)
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_categoryFragment)
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
        binding.reviewAddBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_map_to_reviewAddSearch)
        }

        /**
         * 카테고리 전체 조회 - mindy
         */


        // 나이대로 표현하기
        fun getAgeGroupFromAge(ageText: String): String {
            val age = ageText.toIntOrNull() ?: return "나이 정보가 올바르지 않습니다"

            return when (age) {
                in 10..19 -> "10"
                in 20..29 -> "20"
                in 30..39 -> "30"
                in 40..49 -> "40"
                in 50..59 -> "50"
                in 60..69 -> "60"
                in 70..79 -> "70"
                in 80..89 -> "80"
                in 90..99 -> "90"
                else -> "기타 연령" // 특정 범위에 속하지 않는 경우
            }
        }

        binding.fragmentArea.apply {

            //사용자에 대한 정보 //

            val user_age = binding.root.findViewById<TextView>(R.id.user_age) //화면에 출력되는 나이값
            val age = binding.root.findViewById<TextView>(R.id.ageBtn) //아직 없길래 임의 값.. 불러오기
            //val ageGroup = getAgeGroupFromAge(age) //나이대로 변환
            //user_age.text = ageGroup

            //성별에 대한 정보 불러오기 //

        // 사용자에 대한 정보 가져오기
            gustoViewModel.getUserProfile("my") { result, data ->
                when (result) {
                    1 -> {
                        if (data != null) {
                            // 사용자 정보가 성공적으로 가져온 경우
                            userName1.text = data.nickname
                            userName2.text = data.nickname
                            userName3.text = data.nickname

                        }
                    }
                }
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
            }
        })

    }
    override fun onResume() {
        super.onResume()
        binding.kakaoMap.resume()

        // 카테고리 조회 및 칩 추가
        getMapCategoryAndAddChips("성수1가1동")

        // 데이터 넣어둔 변수 : gustoViewModel.myMapCategoryList
        gustoViewModel.getMapCategory(gustoViewModel.dong.value!!){
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

        //카테고리 변경 데이터//

        //저장 맛집
        var locRestSaveNum = binding.fragmentArea.locRestSaveNum

        //방문 맛집
        var noVisNum = binding.fragmentArea.noVisNum
        var visNum = binding.fragmentArea.visNum


        /**
         * 저장된 맛집 조회 - mindy
         * 현재 카테고리 선택이 구현 보류로 categoryId에 null 넣고 추후 보완 예정
         * live data observe
         */
        //리스트 별로 저장
        // 방문X 리스트 저장 변수 : gustoViewModel.mapUnvisitedList
        // 방문X 개수 : gustoViewModel.mapUnvisitedCnt
        // 방문 O 리스트 저장 변수 : gustoViewModel.mapVisitedList
        // 방문o 개수 : gustoViewModel.mapVisitedCnt
        //닉네임 변수 : gustoViewModel.userNickname

        gustoViewModel.dong.observe(viewLifecycleOwner, Observer {
            gustoViewModel.getSavedStores(gustoViewModel.dong.value!!, null){
                    result ->
                when(result){
                    0 -> {
                        Log.d("viewmodel : vi",gustoViewModel.mapVisitedList.toString())
                        Log.d("viewmodel : novi",gustoViewModel.mapUnvisitedList.toString())
                        //동
                        refindDong()
                    }
                    1 -> {
                        Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }



    override fun onPause() {
        //binding.kakaoMap.removeAllViews()
        super.onPause()
        binding.kakaoMap.pause()
    }

    //현재 동을 불러오기//
    //현재 동에 대한 작업 불러오기//
    fun refindDong(){

        //동
        var dong = binding.fragmentArea.dong
        var areaPick = binding.fragmentArea.areaPick

        //저장 맛집
        var locRestSaveNum = binding.fragmentArea.locRestSaveNum

        //방문 맛집
        var noVisNum = binding.fragmentArea.noVisNum
        var visNum = binding.fragmentArea.visNum


        //출력//
        Log.d("dong", "${dong}")
        dong.text = gustoViewModel.dong.value// 사용자의 현재 동 정보를 가져와서 텍스트뷰에 설정
        areaPick.text = gustoViewModel.dong.value // 사용자의 현재 동 정보를 가져와서 없 텍스트뷰에 설정

        noVisNum.text = gustoViewModel.mapUnvisitedCnt.toString() //방문해본 적는 맛집 수
        visNum.text = gustoViewModel.mapVisitedCnt.toString() //방문해본 적 있는 맛집 수

        var save_rest = gustoViewModel.mapVisitedCnt + gustoViewModel.mapUnvisitedCnt //저장한 맛집 수

        locRestSaveNum.text = save_rest.toString()

        /*
        // 저장된 맛집의 수를 locRestSaveNum 텍스트뷰에 연결
        gustoViewModel.getSavedStores("${dong}", null) { result ->
            when (result) {
                0 -> {
                    // 성공적으로 저장된 맛집 정보를 가져온 경우
                    val savedStoresCount = gustoViewModel.savedStoreIdList.size
                    Log.d("save_rest","${savedStoresCount}")
                    locRestSaveNum.text = savedStoresCount.toString()
                }
                else -> {
                    // 저장된 맛집 정보를 가져오지 못한 경우
                    locRestSaveNum.text = "0"
                    Toast.makeText(context, "저장된 맛집 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
         */



        //사진 불러와서 리사이클러뷰와 연결해 담기//
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager3 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val recyclerView: RecyclerView = binding.fragmentArea.recyclerViewNoVisitedRest
        val recyclerView2: RecyclerView = binding.fragmentArea.recyclerViewVisitedRest
        val recyclerView3: RecyclerView = binding.fragmentArea.recyclerViewAgeNoVisitedRest

        // 아이템 담기
        val itemList_unvisit = ArrayList<String>()
        val itemList_visit = ArrayList<String>()
        val itemList_unvisit_age = ArrayList<String>() //나이대 별로 pick

        val itemList = ArrayList<String>()


        val unvisitedStores = gustoViewModel.mapUnvisitedList
        val visitedStores = gustoViewModel.mapVisitedList
        //val unvisitedStores_age = gustoViewModel.mapVisitedList

        // 방문 X - 각 가게에 대한 정보
        gustoViewModel.mapUnvisitedList?.let { unvisitedStores ->
            Log.d("log_img","방문 안 한 가게 이미지")
            for (store in unvisitedStores) {
                val reviewImg = store.reviewImg
                Log.d("log_img","방문 X 가게 이미지 ${reviewImg}")
                reviewImg?.let { itemList_unvisit.add(it) } // null이 아닌 경우에만 itemList_unvisit에 추가
            }
        }

        // 방문 O - 각 가게에 대한 정보
        gustoViewModel.mapVisitedList?.let { visitedStores ->
            Log.d("log_img","방문 가게 이미지")
            for (store in visitedStores) {
                val reviewImg = store.reviewImg
                Log.d("img","${reviewImg}")
                reviewImg?.let { itemList_visit.add(it) } // null이 아닌 경우에만 itemList에 추가
            }
        }

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


        val adapter = MapRecyclerAdapter(itemList_unvisit)
        val adapter2 = MapRecyclerAdapter(itemList_visit)
        val adapter3 = MapRecyclerAdapter(itemList)
        //val adapter3 = MapRecyclerAdapter(itemList_unvisit_age)

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
                    Log.e(TAG, "onMapDestroy")
                }

                override fun onMapError(error: Exception?) {
                    Log.e(TAG, "onMApError", error)

                }

            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(getKakaoMap: KakaoMap) {
                    kakaoMap = getKakaoMap
                    kakaoMap.setOnCameraMoveEndListener { kakaoMap, cameraPosition, gestureType ->
                        // 카메라 움직임 종료 시 이벤트 호출
                        // 사용자 제스쳐가 아닌 코드에 의해 카메라가 움직이면 GestureType 은 Unknown
                        Log.e(TAG, "cur loc : "+cameraPosition.toString())
                        gustoViewModel.getRegionInfo(cameraPosition.position.longitude, cameraPosition.position.latitude)  {result, address ->
                            when(result) {
                                1 -> {
                                    Log.d(TAG, "gustoViewModel.dong.value")
                                    binding.fragmentArea.userLoc.text = address
                                    refindDong()
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
                            Log.d(TAG,label.tag.toString())
                            binding.vpSlider.currentItem = (label.tag as Int) -1
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
                    Log.e(TAG, "onMapReady")
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
        gustoViewModel.getCurrentMapStores(currentChip,isVisited) {result, datas ->
            when(result) {
                1 -> {
                    markerList.clear()
                    Log.d("SOL_LOG22",datas.toString())
                    if(datas!=null) {
                        for((index,data) in datas.withIndex()) {
                            markerList.add(MarkerItem(data.storeId, index+1,0, data.latitude!!, data.longitude!!, data.storeName!!, "", true))
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


