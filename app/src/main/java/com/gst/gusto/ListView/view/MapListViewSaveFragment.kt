package com.gst.gusto.ListView.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.ListView.adapter.ListViewStoreAdapter
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMapListviewSaveBinding


class MapListViewSaveFragment : Fragment() {

    private lateinit var binding : FragmentMapListviewSaveBinding
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
//        val mSaveOAdapter = ListViewStoreAdapter("save", view)
//        mSaveOAdapter.submitList(sampleSaveOArray)
//        mSaveOAdapter.setItemClickListener(object : ListViewStoreAdapter.OnItemClickListener{
//            override fun onClick(v: View, dataSet: Store) {
//                Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_storeDetailFragment)
//            }
//
//        })
//        binding.rvMapSaveVisited.adapter = mSaveOAdapter
//        binding.rvMapSaveVisited.layoutManager = LinearLayoutManager(this.requireActivity())

        //방문 X Rv 연결
//        val mSaveXAdapter = ListViewStoreAdapter("save", view)
//        mSaveXAdapter.submitList(sampleSaveOArray)
//        mSaveXAdapter.setItemClickListener(object : ListViewStoreAdapter.OnItemClickListener{
//            override fun onClick(v: View, dataSet: Store) {
//                Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_storeDetailFragment)
//            }
//
//        })
//        binding.rvMapSaveUnvisited.adapter = mSaveXAdapter
//        binding.rvMapSaveUnvisited.layoutManager = LinearLayoutManager(this.requireActivity())

        //뒤로가기 클리 리스너
        binding.ivMapMapBack.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_fragment_map)
        }

    }

}