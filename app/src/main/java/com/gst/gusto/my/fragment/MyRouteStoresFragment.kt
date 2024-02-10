package com.gst.gusto.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.databinding.FragmentMyRouteStoresBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter

class MyRouteStoresFragment : Fragment() {

    lateinit var binding: FragmentMyRouteStoresBinding
    val itemList = ArrayList<MarkerItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRouteStoresBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = binding.recyclerView

        itemList.add(MarkerItem(0,0,1.1,1.1,"성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층",false))
        itemList.add(MarkerItem(0,0,1.1,1.1,"성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층",false))
        itemList.add(MarkerItem(0,0,1.1,1.1,"성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층",false))
        itemList.add(MarkerItem(0,0,1.1,1.1,"성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층",false))

        val boardAdapter = MapRoutesAdapter(itemList,binding.lyGone,requireActivity())
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
}