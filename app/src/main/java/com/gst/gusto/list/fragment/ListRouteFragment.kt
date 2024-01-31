package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.databinding.FragmentListRouteBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class ListRouteFragment : Fragment() {

    lateinit var binding: FragmentListRouteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteBinding.inflate(inflater, container, false)

        val rv_board = binding.rvListRoute

        val itemList = ArrayList<GroupItem>()

        itemList.add(GroupItem("성수동 맛집 맵",0,5,0))
        itemList.add(GroupItem("성수동 맛집 맵",0,4,0))
        itemList.add(GroupItem("성수동 맛집 맵",0,2,0))
        itemList.add(GroupItem("성수동 맛집 맵",0,8,0))

        val boardAdapter = LisAdapter(itemList, null, 1)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        return binding.root

    }

}