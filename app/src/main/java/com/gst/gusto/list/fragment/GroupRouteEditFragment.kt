package com.gst.gusto.list.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.FragmentListGroupMRouteEditBinding
import com.gst.gusto.list.adapter.GroupViewpagerAdapter
import com.gst.gusto.list.adapter.LisAdapter
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RouteItem
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter

class GroupRouteEditFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteEditBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("viewpage",1)
            findNavController().navigate(R.id.action_groupMRoutEditFragment_to_groupFragment,bundle)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = ArrayList<RouteItem>()

        itemList.add(RouteItem("성수동 맛집 맵"," "))
        itemList.add(RouteItem("성수동 맛집 맵"," "))
        itemList.add(RouteItem("성수동 맛집 맵"," "))
        itemList.add(RouteItem("성수동 맛집 맵"," "))

        binding.rvRoutes

        val boardAdapter = MapRoutesAdapter(itemList)
        boardAdapter.notifyDataSetChanged()

        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.btnPlus.setOnClickListener {
            if(binding.tvRestName.text.length>0 && itemList.size<6) {
                itemList.add(RouteItem(binding.tvRestName.text.toString(),""))
                boardAdapter.notifyItemInserted(itemList.size-1)
            }
        }

    }



}