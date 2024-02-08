package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class ListGroupFragment : Fragment() {

    lateinit var binding: FragmentListGroupBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupBinding.inflate(inflater, container, false)

        val rv_board = binding.rvListGourp

        val itemList = ArrayList<GroupItem>()

        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))

        fun callActivityFunction(): NavController {
            return (activity as? MainActivity)?.getCon() ?: throw IllegalStateException("NavController is null")
        }

        val boardAdapter = LisAdapter(itemList, callActivityFunction(), 0)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)



        return binding.root

    }
    override fun onResume() {
        super.onResume()
        gustoViewModel.listFragment = "group"
    }
}