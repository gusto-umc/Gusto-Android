package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentListGroupMRouteStoresBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RouteAdapter
import com.gst.gusto.list.adapter.RouteItem

class GroupRouteStoresFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteStoresBinding
    val itemList = ArrayList<RouteItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteStoresBinding.inflate(inflater, container, false)

        val bundle = Bundle()
        binding.fabEdit.setOnClickListener {
            fun callActivityFunction(): NavController {
                return (activity as? MainActivity)?.getCon() ?: throw IllegalStateException("NavController is null")
            }
            bundle.putSerializable("itemList",itemList)
            callActivityFunction().navigate(R.id.action_groupFragment_to_groupMRoutEditFragment,bundle)
        }
        binding.fabMap.setOnClickListener {
            fun callActivityFunction(): NavController {
                return (activity as? MainActivity)?.getCon() ?: throw IllegalStateException("NavController is null")
            }
            bundle.putSerializable("itemList",itemList)
            callActivityFunction().navigate(R.id.action_groupFragment_to_groupMRoutMapFragment,bundle)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = binding.rv

        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))

        val boardAdapter = MapRoutesAdapter(itemList,binding.lyAddRoute)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
}