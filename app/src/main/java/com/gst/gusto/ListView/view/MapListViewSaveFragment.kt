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

    private var categoryId: Int = 1 // 기본값을 설정하거나 적절한 값을 초기화
    private var townName: String = "SampleTown" // 기본값을 설정하거나 적절한 값을 초기화

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

        // 데이터 초기 로딩을 위해 호출
        //gustoViewModel.resetData(categoryId, townName)

        // 데이터 변경을 관찰하고 어댑터에 새 데이터를 설정
        gustoViewModel.storeList.observe(viewLifecycleOwner, Observer { stores ->
            adapter.submitList(stores)
            Log.d("viewModelStore", "어댑터 설정")
        })

        // RecyclerView의 스크롤 리스너를 설정
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

