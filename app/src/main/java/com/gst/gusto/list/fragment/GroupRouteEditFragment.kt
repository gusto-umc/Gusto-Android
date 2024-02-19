package com.gst.gusto.list.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMRouteEditBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
import net.daum.mf.map.api.MapView

class GroupRouteEditFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteEditBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var mapView : MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteEditBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = gustoViewModel.markerListLiveData.value as ArrayList

        val boardAdapter = MapRoutesAdapter(itemList,binding.lyAddRoute,requireActivity(),0)
        boardAdapter.notifyDataSetChanged()

        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.btnPlus.setOnClickListener {
            itemList.add(MarkerItem(
                0,
                0,0,
                37.6219001,
                127.0743010,
                binding.tvRestName.text.toString(),
                "",
                false
            ))
            gustoViewModel.markerListLiveData.value = itemList
            boardAdapter.notifyItemInserted(itemList.size-1)
            if(itemList.size==6) {
                binding.lyAddRoute.visibility = View.INVISIBLE
            }
        }

        mapView = MapView(requireContext())

        mapUtil.setMapInit(mapView, binding.kakaoRouteMap, requireContext(), requireActivity(),"route",this)
        //mapUtil.setRoute(mapView, itemList)

        gustoViewModel.markerListLiveData.observe(viewLifecycleOwner, Observer { markers ->
            Log.e("erroe","dsaasd")
            mapUtil.setRoute(mapView, markers)
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("e",gustoViewModel.markerListLiveData.value.toString())
        gustoViewModel.groupFragment = 1
    }
    override fun onPause() {
        super.onPause()
        binding.kakaoRouteMap.removeAllViews()
    }

}