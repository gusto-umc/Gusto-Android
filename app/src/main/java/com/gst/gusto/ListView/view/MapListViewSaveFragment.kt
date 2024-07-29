package com.gst.gusto.ListView.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.adapter.SavedStoreListAdapter
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseSavedStoreData
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

        // RecyclerView 설정
        setupRecyclerView(view)

        // 방문한 식당 목록을 가져옴
        //loadInitialData()

        /*
        // 방문한 식당 목록 연결
        gustoViewModel.visitedStoresList.observe(viewLifecycleOwner, Observer { visitedStores ->
            // 데이터가 업데이트될 때 어댑터에 새 데이터를 설정
            adapter.submitList(visitedStores)
        })

        // hasNext 플래그에 따라 추가 데이터를 로드
        gustoViewModel.hasNext.observe(viewLifecycleOwner, Observer { hasNext ->
            if (hasNext) {
                binding.rvMapSaveVisited.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val totalItemCount = layoutManager.itemCount
                        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                        if (hasNext && lastVisibleItem >= totalItemCount - 1) {
                            loadMoreData()
                        }
                    }
                })
            }
        })
        */
    }

    // RecyclerView 설정 함수
    private fun setupRecyclerView(rootView: View) {
        adapter = SavedStoreListAdapter("save", rootView)
        adapter.mContext = context

        adapter.setItemClickListener(object : SavedStoreListAdapter.OnItemClickListener {
            override fun onClick(dataSet: ResponseSavedStoreData) {
                gustoViewModel.selectStoreId = dataSet.storeId.toLong()
                gustoViewModel.storeIdList.clear()
                gustoViewModel.storeIdList = gustoViewModel.savedStoreIdList
                Navigation.findNavController(rootView).navigate(R.id.action_mapListViewSaveFragment_to_fragment_map_viewpager)
            }
        })

        binding.rvMapSaveVisited.adapter = adapter
        binding.rvMapSaveVisited.layoutManager = LinearLayoutManager(requireContext())
    }

    /*
    // 초기 데이터 로드
    private fun loadInitialData() {
        gustoViewModel.loadVisitedStores(gustoViewModel.selectedCategoryId, gustoViewModel.currentTownName)
    }

    // 추가 데이터 로드
    private fun loadMoreData() {
        gustoViewModel.loadVisitedStores(gustoViewModel.selectedCategoryId, gustoViewModel.currentTownName)
    }

     */
}
