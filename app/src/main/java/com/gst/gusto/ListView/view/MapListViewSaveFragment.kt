package com.gst.gusto.ListView.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.ListView.adapter.ListViewStoreAdapter
import com.gst.gusto.ListView.adapter.SavedStoreListAdapter
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseSavedStoreData
import com.gst.gusto.databinding.FragmentMapListviewSaveBinding


class MapListViewSaveFragment : Fragment() {

    private lateinit var binding : FragmentMapListviewSaveBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private var sampleSaveOArray = arrayListOf<Store>(
        Store(id = 0, storeName = "구스토 레스토랑", location = "메롱시 메로나동 바밤바 24-6 1층", visitCount = null, storePhoto = 1, serverCategory ="양식", isSaved = false),
        Store(id = 1, storeName = "Gusto Restaurant", location = "메롱시 메로나동 바밤바 24-6 1층", visitCount = null, storePhoto = 1, serverCategory = "양식", isSaved = true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_listview_save, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //방문 O Rv 연결
        val mSaveOAdapter = SavedStoreListAdapter("save", view)
        mSaveOAdapter.mContext = context
        mSaveOAdapter.submitList(gustoViewModel.mapVisitedList)
        mSaveOAdapter.setItemClickListener(object : SavedStoreListAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: ResponseSavedStoreData) {
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
                gustoViewModel.selectedDetailStoreId = dataSet.storeId
                Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_fragment_map_viewpager)
            }

        })
        binding.rvMapSaveUnvisited.adapter = mSaveXAdapter
        binding.rvMapSaveUnvisited.layoutManager = LinearLayoutManager(this.requireActivity())

        //뒤로가기 클리 리스너
        binding.ivMapMapBack.setOnClickListener{
            findNavController().popBackStack()
        }

    }

}