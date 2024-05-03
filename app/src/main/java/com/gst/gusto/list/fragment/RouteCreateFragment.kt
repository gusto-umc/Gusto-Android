package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.RequestCreateRoute
import com.gst.gusto.api.RouteList
import com.gst.gusto.databinding.FragmentListRouteCreateBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter

class RouteCreateFragment : Fragment() {

    lateinit var binding: FragmentListRouteCreateBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private val itemList = ArrayList<MarkerItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteCreateBinding.inflate(inflater, container, false)

        gustoViewModel.listFragment = "route"

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener {
            val routeList = ArrayList<RouteList>()
            for((index,data) in itemList.withIndex()) {
                routeList.add(RouteList(data.storeId,index+1,null,null,null,null,null))
            }
            gustoViewModel.requestRoutesData = RequestCreateRoute(binding.etRouteName.text.toString(),null,routeList)
            gustoViewModel.createRoute {result ->
                when(result) {
                    1 -> {
                        gustoViewModel.requestRoutesData = null
                        findNavController().popBackStack()
                    }
                }
            }

        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gustoViewModel.requestRoutesData = null

        val boardAdapter = MapRoutesAdapter(itemList,binding.lyAddRoute,requireActivity(),0)
        boardAdapter.notifyDataSetChanged()
        //gustoViewModel.groupRouteCreateFragment = this

        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.btnPlus.setOnClickListener {
            findNavController().navigate(R.id.action_routeCreateFragment_to_routeSearchFragment)

        }
        addStore()
    }

    override fun onDestroy() {
        super.onDestroy()
        gustoViewModel.listFragment="route"
    }
    fun addStore() {
        if(gustoViewModel.routeStorTmpData!=null) {
            var data = gustoViewModel.routeStorTmpData
            if (data != null) {
                itemList.add(MarkerItem(data.storeId.toLong(), 0, 0,1.1, 1.1, data.storeName, "", false))
            }
            binding.rvRoutes.adapter?.notifyItemInserted(itemList.size-1)
            if(itemList.size==6) {
                binding.lyAddRoute.visibility = View.INVISIBLE
            }
        }
    }


}