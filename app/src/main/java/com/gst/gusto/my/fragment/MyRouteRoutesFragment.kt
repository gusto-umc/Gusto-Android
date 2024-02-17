package com.gst.gusto.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMyRouteRoutesBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class MyRouteRoutesFragment : Fragment() {

    lateinit var binding: FragmentMyRouteRoutesBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRouteRoutesBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = binding.recyclerView

        val itemList = ArrayList<GroupItem>()

        itemList.add(GroupItem(0, "성수동 맛집 맵", 5, true,32, 24))
        itemList.add(GroupItem(0, "성수동 맛집 맵", 5, true,32, 24))
        itemList.add(GroupItem(0, "성수동 맛집 맵", 5, true,32, 24))


        val boardAdapter = LisAdapter(itemList, null, 3, gustoViewModel,null)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
}