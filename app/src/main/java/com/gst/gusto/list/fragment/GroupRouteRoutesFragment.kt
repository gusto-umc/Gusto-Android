package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.databinding.FragmentListGroupMRouteRoutesBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class GroupRouteRoutesFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteRoutesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteRoutesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = binding.rv

        val itemList = ArrayList<GroupItem>()

        itemList.add(GroupItem("성수동 맛집 맵",0,5,0))
        itemList.add(GroupItem("성수동 맛집 맵",0,4,24))
        itemList.add(GroupItem("성수동 맛집 맵",0,2,0))
        itemList.add(GroupItem("성수동 맛집 맵",0,8,24))

        val boardAdapter = LisAdapter(itemList,null,2)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }


}