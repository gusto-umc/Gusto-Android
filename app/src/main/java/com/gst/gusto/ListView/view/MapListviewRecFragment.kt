package com.gst.gusto.ListView.view

import android.os.Bundle
import android.provider.ContactsContract.RawContacts.Data
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.adapter.SavedStoreListAdapter
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseSavedStoreData
import com.gst.gusto.databinding.FragmentMapListviewRecBinding

class MapListviewRecFragment : Fragment() {

    private lateinit var binding : FragmentMapListviewRecBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
         * rv 데이터 넣기
         * 현재 : 임시로 추천 맛집 데이터 받아옴, 추후 api 연결 필요
         */
        val mRecAdapter = SavedStoreListAdapter("save", view)
        mRecAdapter.mContext = context
        mRecAdapter.submitList(gustoViewModel.mapVisitedList)
        mRecAdapter.setItemClickListener(object : SavedStoreListAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: ResponseSavedStoreData) {
                gustoViewModel.selectStoreId = dataSet.storeId.toLong()
                gustoViewModel.storeIdList.clear()
                gustoViewModel.storeIdList = gustoViewModel.savedStoreIdList
                Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_fragment_map_viewpager)
            }

        })
        binding.rvMapRec.adapter = mRecAdapter
        binding.rvMapRec.layoutManager = LinearLayoutManager(this.requireActivity())

        /**
         * 지도보기 클릭 리스너
         * api 로직 변경 예정이라서 추후 보완 예정
         */
        binding.fabMapRecMap.setOnClickListener{
            //데이터 저장
            //gustoViewModel.selectStoreId = gustoViewModel.mapKeepStoreIdArray[0]
            //gustoViewModel.storeIdList = gustoViewModel.mapKeepStoreIdArray

            //페이지 이동
            Navigation.findNavController(view).navigate(R.id.action_mapListviewRecFragment_to_fragment_map_viewpager)
        }
    }
}