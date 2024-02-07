package com.gst.gusto.my

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMyRouteStoresBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RouteAdapter
import com.gst.gusto.list.adapter.RouteItem
import com.gst.gusto.review.adapter.GalleryReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration

class MyRouteStoresFragment : Fragment() {

    lateinit var binding: FragmentMyRouteStoresBinding
    val itemList = ArrayList<RouteItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRouteStoresBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = binding.recyclerView

        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))
        itemList.add(RouteItem("성수동 맛집 맵","메롱시 메로나동 바밤바 24-6 1층"))

        val boardAdapter = MapRoutesAdapter(itemList,binding.lyGone)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
}