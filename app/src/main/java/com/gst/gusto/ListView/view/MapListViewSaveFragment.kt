package com.gst.gusto.ListView.view

import SavedStoreListAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.StoreData
import com.gst.gusto.databinding.FragmentMapListviewSaveBinding

class MapListViewSaveFragment : Fragment() {

    private lateinit var binding: FragmentMapListviewSaveBinding
    private val gustoViewModel: GustoViewModel by activityViewModels()
    private lateinit var adapter: SavedStoreListAdapter

    // category Id와 town Name 초기화
    private var categoryId: Int? = null
    private var townName: String = "성수1가1동" // 기본값

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapListviewSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupScrollListener()

        // 데이터 초기 로딩: 방문 식당 필터 설정
        gustoViewModel.setSaveFilters(categoryId, townName)  // 방문식당 조회를 위한 필터 설정
        Log.d("viewModelStore", "categoryId: $categoryId")
        Log.d("viewModelStore", "townName: $townName")

        // ViewModel에서 방문 식당 데이터 관찰
        gustoViewModel.savedStores.observe(viewLifecycleOwner, Observer { stores ->
            adapter.submitList(stores) // 방문 식당 리스트를 어댑터에 전달
        })

        // hasNext 값 변경을 통해 추가 데이터 로드 여부 결정
        gustoViewModel.hasNext.observe(viewLifecycleOwner, Observer { hasNext ->
            Log.d("viewModelStore", "hasNext 값: $hasNext")
            // hasNext 값이 변경될 때 스크롤 리스너가 올바르게 작동하도록 설정
            setupScrollListener()
        })

        // 아이템 클릭 리스너 설정
        adapter.setItemClickListener(object : SavedStoreListAdapter.OnItemClickListener {
            override fun onClick(dataSet: StoreData) {
                Log.d("viewModelStore", "아이템클릭리스너")
                gustoViewModel.selectedStoreData = dataSet
                gustoViewModel.storeIdList.clear()
                gustoViewModel.storeIdList = gustoViewModel.savedStoreIdList
                // 다른 프래그먼트로 이동
                Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_fragment_map_viewpager)
            }
        })
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {
        adapter = SavedStoreListAdapter()
        binding.rvMapSaveVisited.adapter = adapter
        binding.rvMapSaveVisited.layoutManager = LinearLayoutManager(requireContext())
    }

    // 스크롤 리스너 설정
    private fun setupScrollListener() {
        binding.rvMapSaveVisited.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // 스크롤이 끝에 도달했을 때 추가 데이터 로드
                if (gustoViewModel.hasNext.value == true && lastVisibleItem >= totalItemCount - 1 && !gustoViewModel.isLoading) {
                    showLoadingIndicator() // 로딩 인디케이터를 표시
                    // 1초 지연 후 다음 페이지 로드
                    Handler(Looper.getMainLooper()).postDelayed({
                        gustoViewModel.tapSavedStores() // 다음 페이지 로드
                        hideLoadingIndicator() // 로딩 인디케이터 숨기기
                    }, 1000) // 1000 밀리초 = 1초
                }
            }
        })
    }

    // 로딩 인디케이터 표시
    private fun showLoadingIndicator() {
        binding.progressBar.visibility = View.VISIBLE
    }

    // 로딩 인디케이터 숨기기
    private fun hideLoadingIndicator() {
        binding.progressBar.visibility = View.GONE
    }
}
