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
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMRouteEditBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RouteItem

class GroupRouteEditFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteEditBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

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

        val itemList = ArrayList<RouteItem>()

        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))

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

    override fun onDestroy() {
        super.onDestroy()
        gustoViewModel.groupFragment = 1
    }

}