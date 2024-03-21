package com.gst.gusto.list.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.RequestCreateRoute
import com.gst.gusto.api.RouteList
import com.gst.gusto.databinding.FragmentListGroupMRouteCreateBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter

class GroupRouteCreateFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteCreateBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteCreateBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gustoViewModel.requestRoutesData = null

        val boardAdapter = MapRoutesAdapter(gustoViewModel.itemList,binding.lyAddRoute,requireActivity(),0)
        boardAdapter.notifyDataSetChanged()

        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        if(gustoViewModel.routeStorTmpData != null) {
            var data = gustoViewModel.routeStorTmpData
            if (data != null) {
                gustoViewModel.itemList.add(MarkerItem(data.storeId.toLong(), 0, 0,1.1, 1.1, data.storeName, "", false))
            }
            boardAdapter.notifyItemInserted(gustoViewModel.itemList.size-1)
            if(gustoViewModel.itemList.size==6) {
                binding.lyAddRoute.visibility = View.INVISIBLE
            }
            if(gustoViewModel.tmpName!="") binding.etRouteName.setText(gustoViewModel.tmpName)
            gustoViewModel.routeStorTmpData = null
        }
        binding.btnPlus.setOnClickListener {
            gustoViewModel.groupFragment = 2
            if(binding.etRouteName.text.toString()!="") gustoViewModel.tmpName = binding.etRouteName.text.toString()
            (requireActivity() as MainActivity).getCon().navigate(R.id.action_groupFragment_to_routeSearchFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        fun callActivityFunction(): NavHostFragment {
            return (activity as? MainActivity)?.getNavHost() ?: throw IllegalStateException("NavController is null")
        }
        val parent = callActivityFunction().childFragmentManager.fragments[0] as GroupFragment
        parent.binding.btnSave.visibility =View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        Log.d("viewmodel","pase")
    }
    fun getRequestRoutesData() {
        val routeList = ArrayList<RouteList>()
        for((index,data) in gustoViewModel.itemList.withIndex()) {
            routeList.add(RouteList(data.storeId,index+1,null,null,null,null,null))
        }
        gustoViewModel.requestRoutesData = RequestCreateRoute(binding.etRouteName.text.toString(),gustoViewModel.currentGroupId,routeList)
    }



}