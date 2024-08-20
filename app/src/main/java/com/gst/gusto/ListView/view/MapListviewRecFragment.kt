package com.gst.gusto.ListView.view

import SavedStoreListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.StoreData
import com.gst.gusto.databinding.FragmentMapListviewRecBinding

class MapListviewRecFragment : Fragment() {

    private lateinit var binding: FragmentMapListviewRecBinding
    private val gustoViewModel: GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_listview_rec, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 뒤로 가기 클릭 리스너
         */
        binding.ivMapRecBack.setOnClickListener {
            findNavController().popBackStack()
        }

        /**
         * 동 이름 넣기
         */
        binding.tvMapRecDong.text = gustoViewModel.dong.value

        /**
         * rv 데이터 설정
         * 데이터는 ViewModel에서 가져오고, LiveData를 통해 업데이트합니다.
         */
        val mRecAdapter = SavedStoreListAdapter()
        mRecAdapter.setItemClickListener(object : SavedStoreListAdapter.OnItemClickListener {
            override fun onClick(dataSet: StoreData) {
                //gustoViewModel.selectStoreId = dataSet.storeId
                gustoViewModel.storeIdList.clear()
                gustoViewModel.storeIdList.addAll(gustoViewModel.savedStoreIdList)
                findNavController().navigate(R.id.action_mapListViewSaveFragment_to_fragment_map_viewpager)
            }
        })

        binding.rvMapRec.apply {
            adapter = mRecAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // ViewModel의 storeList와 RecyclerView를 연결
        gustoViewModel.storeList.observe(viewLifecycleOwner) { stores ->
            mRecAdapter.submitList(stores)
        }

        /**
         * 지도보기 클릭 리스너
         * 페이지 이동 설정
         */
        binding.fabMapRecMap.setOnClickListener {
            // 페이지 이동
            findNavController().navigate(R.id.action_mapListviewRecFragment_to_fragment_map_viewpager)
        }
    }
}
