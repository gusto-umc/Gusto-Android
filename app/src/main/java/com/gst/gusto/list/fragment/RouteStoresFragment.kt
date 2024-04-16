package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListRouteStoresBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter

class RouteStoresFragment : Fragment() {

    lateinit var binding: FragmentListRouteStoresBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private var itemList = ArrayList<MarkerItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteStoresBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabEdit.setOnClickListener {
            gustoViewModel.getRouteMap() { result ->
                when (result) {
                    1 -> {
                        gustoViewModel.editMode = true
                        findNavController().navigate(R.id.action_routeStoresFragment_to_groupMRMFragment)
                    }
                    else -> {
                        Toast.makeText(context,"서버와의 연결 불안정", Toast.LENGTH_SHORT ).show()
                    }
                }
            }
        }

        binding.fabMap.setOnClickListener {
            gustoViewModel.getRouteMap() { result ->
                when (result) {
                    1 -> {
                        findNavController().navigate(R.id.action_routeStoresFragment_to_groupMRMFragment)
                    }
                    else -> {
                        Toast.makeText(context,"서버와의 연결 불안정", Toast.LENGTH_SHORT ).show()
                    }
                }
            }

        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = gustoViewModel.markerListLiveData.value!!
        val boardAdapter = MapRoutesAdapter(itemList,binding.lyNull,requireActivity(),0)
        boardAdapter.notifyDataSetChanged()

        binding.tvRouteName.text = gustoViewModel.routeName
        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        gustoViewModel.groupFragment = 0
        gustoViewModel.listFragment="route"
        //gustoViewModel.markerListLiveData.value?.clear()
    }


}