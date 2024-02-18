package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.RequestCreateRoute
import com.gst.gusto.api.RouteList
import com.gst.gusto.databinding.FragmentListRouteCreateBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter

class RouteCreateFragment : Fragment() {

    lateinit var binding: FragmentListRouteCreateBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private var tmp = 1
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
            for(data in itemList) {
                routeList.add(RouteList(data.storeId,data.ordinal,null,null,null,null,null))
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

        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.btnPlus.setOnClickListener {
            itemList.add(MarkerItem(tmp.toLong(), tmp, 0,1.1, 1.1, binding.tvRestName.text.toString(), "", false))
            tmp++
            boardAdapter.notifyItemInserted(itemList.size-1)
            if(itemList.size==6) {
                binding.lyAddRoute.visibility = View.INVISIBLE
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        gustoViewModel.listFragment="route"
    }


}