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
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListRouteStoresBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
class RouteStoresFragment : Fragment() {

    lateinit var binding: FragmentListRouteStoresBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private val itemList = ArrayList<MarkerItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteStoresBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabEdit.setOnClickListener {
            gustoViewModel.markerListLiveData.value = itemList
            val bundle = Bundle()
            bundle.putBoolean("edit",true)
            findNavController().navigate(R.id.action_routeStoresFragment_to_groupMRMFragment,bundle)
        }

        binding.fabMap.setOnClickListener {
            gustoViewModel.markerListLiveData.value = itemList
            findNavController().navigate(R.id.action_routeStoresFragment_to_groupMRMFragment)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boardAdapter = MapRoutesAdapter(itemList,binding.lyNull,requireActivity())
        boardAdapter.notifyDataSetChanged()

        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        itemList.add(MarkerItem(0,0,37.6215101, 127.0751410,"성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층",false))
        itemList.add(MarkerItem(0,0,37.6245301, 127.0740210,"성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층",false))
        itemList.add(MarkerItem(0,0,37.6215001, 127.0743010,"성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층",false))
    }

    override fun onResume() {
        super.onResume()

    }
    override fun onDestroy() {
        super.onDestroy()
        gustoViewModel.groupFragment = 0
        gustoViewModel.listFragment="route"
        gustoViewModel.markerListLiveData.value?.clear()
    }


}