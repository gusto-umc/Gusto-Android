package com.gst.gusto.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMyRouteStoresBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter

class MyRouteStoresFragment : Fragment() {

    lateinit var binding: FragmentMyRouteStoresBinding
    private var itemList = ArrayList<MarkerItem>()
    private val gustoViewModel : GustoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRouteStoresBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemList = gustoViewModel.markerListLiveData.value!!
        val nickname = gustoViewModel.profileNickname
        if(nickname!="") {
            val boardAdapter = MapRoutesAdapter(itemList,binding.lyGone,requireActivity(),2)
            boardAdapter.notifyDataSetChanged()

            binding.recyclerView.adapter = boardAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        } else {
            val boardAdapter = MapRoutesAdapter(itemList,binding.lyGone,requireActivity(),1)
            boardAdapter.notifyDataSetChanged()

            binding.recyclerView.adapter = boardAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }
}