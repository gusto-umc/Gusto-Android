package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentListRouteCreateBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RouteItem

class RouteCreateFragment : Fragment() {

    lateinit var binding: FragmentListRouteCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteCreateBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("page",1)
            findNavController().navigate(R.id.action_routeCreateFragment_to_listFragment,bundle)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = ArrayList<RouteItem>()
/*

        itemList.add(RouteItem("성수동 맛집 맵"," "))
        itemList.add(RouteItem("성수동 맛집 맵"," "))
        itemList.add(RouteItem("성수동 맛집 맵"," "))
        itemList.add(RouteItem("성수동 맛집 맵"," "))
*/

        binding.rvRoutes

        val boardAdapter = MapRoutesAdapter(itemList,binding.lyAddRoute)
        boardAdapter.notifyDataSetChanged()

        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.btnPlus.setOnClickListener {
            itemList.add(RouteItem(binding.tvRestName.text.toString(),""))
            boardAdapter.notifyItemInserted(itemList.size-1)
            if(itemList.size==6) {
                binding.lyAddRoute.visibility = View.INVISIBLE
            }
        }

    }



}