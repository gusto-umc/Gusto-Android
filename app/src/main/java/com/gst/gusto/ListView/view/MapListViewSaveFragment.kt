package com.gst.gusto.ListView.view

import SavedStoreListAdapter
import android.os.Bundle
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


    // category Id와 town Name만 제대로 불러오면 성공!!

    private var categoryId: Int? = null // 기본값을 설정하거나 적절한 값을 초기화
    private var townName: String = "성수1가1동" // 기본값을 설정하거나 적절한 값을 초기화
    //private var townName: String = gustoViewModel.dong.toString()
    //private var categoryId: Int? = gustoViewModel.category

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

        // 데이터 초기 로딩
        gustoViewModel.setSaveFilters(categoryId, townName)
        Log.d("viewModelStore", "categoryId: ${categoryId}")
        Log.d("viewModelStore", "townName: ${townName}")

        gustoViewModel.stores.observe(viewLifecycleOwner, Observer { stores ->
            Log.d("viewModelStore", "데이터 변경: ${stores.size}개의 식당 데이터")
            adapter.submitList(stores)
        })

        // hasNext 변경을 관찰하여 스크롤 리스너 설정
        gustoViewModel.hasNext.observe(viewLifecycleOwner, Observer { hasNext ->
            Log.d("viewModelStore", "hasNext 값: $hasNext")
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

                // 마지막 아이템이 보일 때 추가 데이터 로딩
                if (gustoViewModel.hasNext.value == true && lastVisibleItem >= totalItemCount - 1) {
                    gustoViewModel.tapSavedStores()
                }
            }
        })
    }
}

