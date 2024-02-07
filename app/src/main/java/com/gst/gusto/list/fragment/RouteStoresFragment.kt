package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentListRouteStoresBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RouteItem

class RouteStoresFragment : Fragment() {

    lateinit var binding: FragmentListRouteStoresBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteStoresBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("page",1)
            findNavController().navigate(R.id.action_routeStoresFragment_to_listFragment,bundle)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = ArrayList<RouteItem>()

        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))

        binding.rvRoutes

        val boardAdapter = MapRoutesAdapter(itemList,binding.lyNull)
        boardAdapter.notifyDataSetChanged()

        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.fabEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("page","route")
            findNavController().navigate(R.id.action_routeStoresFragment_to_groupMREFragment,bundle)
        }

        binding.fabMap.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("page","route")
            bundle.putSerializable("itemList",itemList)
            findNavController().navigate(R.id.action_routeStoresFragment_to_groupMRMFragment,bundle)
        }

    }



}