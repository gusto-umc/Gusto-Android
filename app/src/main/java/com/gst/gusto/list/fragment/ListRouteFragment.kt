package com.gst.clock.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListRouteBinding
import com.gst.gusto.list.ListFragment
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class ListRouteFragment : Fragment() {

    lateinit var binding: FragmentListRouteBinding
    lateinit var itemList : ArrayList<GroupItem>
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv_board = binding.rvListRoute

        fun callActivityFunction(): NavController {
            return (activity as? MainActivity)?.getCon() ?: throw IllegalStateException("NavController is null")
        }/*
        itemList = ArrayList()
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))
        itemList.add(GroupItem("성수동 맛집 맵",5,32,24))

        val boardAdapter = LisAdapter(itemList, callActivityFunction(), 1,gustoViewModel)
        boardAdapter.notifyDataSetChanged()
        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)*/

        gustoViewModel.getTokens(requireActivity() as MainActivity)
        gustoViewModel.checkMyRoute {result ->
            when(result) {
                1 -> {
                    itemList = gustoViewModel.myRouteList
                    val boardAdapter = LisAdapter(itemList, callActivityFunction(), 1, gustoViewModel)
                    boardAdapter.notifyDataSetChanged()
                    rv_board.adapter = boardAdapter
                    rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                } else -> {

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gustoViewModel.listFragment = "route"
    }

    override fun onDestroy() {
        super.onDestroy()
        val frag = requireParentFragment().parentFragment as ListFragment
        Log.e("frag",frag.toString())
        frag.callBtnGroup()
    }
}