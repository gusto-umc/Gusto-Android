package com.gst.gusto.ListView.view

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
import com.gst.gusto.ListView.adapter.SavedStoreListAdapter
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.StoreData
import com.gst.gusto.databinding.FragmentMapListviewSaveBinding

class MapListViewSaveFragment : Fragment() {

    private lateinit var binding: FragmentMapListviewSaveBinding
    private val gustoViewModel: GustoViewModel by activityViewModels()
    private lateinit var adapter: SavedStoreListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapListviewSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("viewModelStore", "프레그먼트 생성")
        setupRecyclerView()
        Log.d("viewModelStore", "리사이클러뷰")

        // Initialize data loading
        val categoryId = 1 // or get from arguments or other sources
        val townName = "SampleTown" // or get from arguments or other sources
        gustoViewModel.resetData(categoryId, townName)
        Log.d("viewModelStore", "데이터 받아오기")

        // Observe data changes
        gustoViewModel.storeList.observe(viewLifecycleOwner, Observer { stores ->
            Log.d("viewModelStore", "어댑터 전")
            Log.d("viewModelStore", "받은 데이터: $stores")
            adapter.submitList(stores)
            Log.d("viewModelStore", "어댑터 후")
        })

        // RecyclerView의 ScrollListener를 onViewCreated에서 설정
        gustoViewModel.hasNext.observe(viewLifecycleOwner, Observer { hasNext ->
            Log.d("viewModelStore", "hasNext value: $hasNext") // Add this log to see the value
            binding.rvMapSaveVisited.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    Log.d("viewModelStore", "해쉬넥스트")
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    Log.d("viewModelStore", "if문")
                    if (hasNext && lastVisibleItem >= totalItemCount - 1) {
                        Log.d("viewModelStore", "Loading more data")
                        val categoryId = 1 // 실제 categoryId 사용
                        val townName = "SampleTown" // 실제 townName 사용
                        gustoViewModel.loadVisitedStores(categoryId, townName)
                    }
                }
            })
        })

        adapter.setItemClickListener(object : SavedStoreListAdapter.OnItemClickListener {
            override fun onClick(dataSet: StoreData) {
                gustoViewModel.selectStoreId = dataSet.storeId
                gustoViewModel.storeIdList.clear()
                gustoViewModel.storeIdList = gustoViewModel.savedStoreIdList
                Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_fragment_map_viewpager)
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = SavedStoreListAdapter()
        binding.rvMapSaveVisited.adapter = adapter // 어댑터 연결
        binding.rvMapSaveVisited.layoutManager = LinearLayoutManager(requireContext()) // 레이아웃 매니저 연결
    }
}
