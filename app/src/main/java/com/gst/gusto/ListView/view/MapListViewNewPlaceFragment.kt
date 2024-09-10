package com.gst.gusto.ListView.view

import NewPlaceAdapter
import android.os.Bundle
import android.util.Log // 로그 사용을 위해 import 추가
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.StoreData
import com.gst.gusto.databinding.FragmentMapListViewNewPlaceBinding

class MapListViewNewPlaceFragment : Fragment() {

    private var _binding: FragmentMapListViewNewPlaceBinding? = null
    private val binding get() = _binding!!

    private val gustoViewModel: GustoViewModel by activityViewModels()
    private lateinit var newPlaceAdapter: NewPlaceAdapter

    private var categoryId: Int? = null
    private var townName: String = "성수1가1동" //기본값

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapListViewNewPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        // 미방문 식당 데이터 로드
        Log.d("MapListViewNewPlace", "Selected Category ID: $categoryId, Town Name: $townName")
        gustoViewModel.setUnsaveFilters(categoryId, townName)
    }

    // MapListViewNewPlaceFragment
    private fun setupObservers() {
        gustoViewModel.unsavedStores.observe(viewLifecycleOwner, Observer { newStores ->
            Log.d("MapListViewNewPlace", "Unsaved Stores Loaded: ${newStores.size} stores")

            // 기존 데이터와 새로운 데이터 병합 (중복 제거)
            val currentList = newPlaceAdapter.getCurrentList().toMutableList()
            newStores.forEach { store ->
                if (store !in currentList) {
                    currentList.add(store)
                }
            }

            // 어댑터에 데이터 전달
            newPlaceAdapter.submitList(currentList)
        })

        // 추가 로딩 여부 옵저버
        gustoViewModel.hasNext.observe(viewLifecycleOwner, Observer { hasNext ->
            Log.d("MapListViewNewPlace", "Has Next Page: $hasNext")
            // 페이지네이션 처리
        })

        // 아이템 클릭 리스너 설정
        newPlaceAdapter.setItemClickListener(object : NewPlaceAdapter.OnItemClickListener {
            override fun onClick(dataSet: StoreData) {
                Log.d("MapListViewNewPlace", "Selected Store: ${dataSet.storeName}")
                // 미방문 식당 클릭 처리
                gustoViewModel.selectedStoreData = dataSet
                gustoViewModel.storeIdList.clear()
                gustoViewModel.storeIdList = gustoViewModel.unsavedStoreIdList
            }
        })
    }

    private fun setupRecyclerView() {
        newPlaceAdapter = NewPlaceAdapter()
        binding.rvMapSaveUnvisited.adapter = newPlaceAdapter
        binding.rvMapSaveUnvisited.layoutManager = LinearLayoutManager(requireContext())

        // 스크롤 리스너 설정
        binding.rvMapSaveUnvisited.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // 마지막 아이템이 보일 때 추가 데이터 로딩
                if (gustoViewModel.hasNext.value == true && !gustoViewModel.isLoading && lastVisibleItem >= totalItemCount - 1) {
                    Log.d("MapListViewNewPlace", "Loading more unsaved stores...")
                    gustoViewModel.tapUnsavedStores()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
