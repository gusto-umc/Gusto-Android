package com.gst.gusto.ListView.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.ListView.adapter.SavedStoreListAdapter
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseSavedStoreData
import com.gst.gusto.databinding.FragmentMapListviewSaveBinding


class MapListViewSaveFragment : Fragment() {

    private lateinit var binding : FragmentMapListviewSaveBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapListviewSaveBinding.inflate(inflater, container, false)
        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_listview_save, container, false) as FragmentMapListviewSaveBinding

        val view = binding.root


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //방문 O Rv 연결
        val mSaveOAdapter = SavedStoreListAdapter("save", view)
        mSaveOAdapter.mContext = context
        mSaveOAdapter.submitList(gustoViewModel.mapVisitedList)
        mSaveOAdapter.setItemClickListener(object : SavedStoreListAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: ResponseSavedStoreData) {
                gustoViewModel.selectStoreId = dataSet.storeId.toLong()
                gustoViewModel.storeIdList.clear()
                gustoViewModel.storeIdList = gustoViewModel.savedStoreIdList
                Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_fragment_map_viewpager)
            }

        })
        binding.rvMapSaveVisited.adapter = mSaveOAdapter
        binding.rvMapSaveVisited.layoutManager = LinearLayoutManager(this.requireActivity())

        //방문 X Rv 연결
        val mSaveXAdapter = SavedStoreListAdapter("save", view)
        mSaveXAdapter.mContext = context
        mSaveXAdapter.submitList(gustoViewModel.mapUnvisitedList)
        mSaveXAdapter.setItemClickListener(object : SavedStoreListAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: ResponseSavedStoreData) {
                gustoViewModel.selectStoreId = dataSet.storeId.toLong()
                gustoViewModel.storeIdList.clear()
                gustoViewModel.storeIdList = gustoViewModel.unsavedStoreIdList
                Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_fragment_map_viewpager)
            }

        })
        binding.rvMapSaveUnvisited.adapter = mSaveXAdapter
        binding.rvMapSaveUnvisited.layoutManager = LinearLayoutManager(this.requireActivity())

        /*
        //뒤로가기 클리 리스너
        binding.ivMapMapBack.setOnClickListener{
            findNavController().popBackStack()
        }

        //현재 위치(동) 받아오기
        binding.tvMapSaveDong.text = gustoViewModel.dong.value


         */
    }


}