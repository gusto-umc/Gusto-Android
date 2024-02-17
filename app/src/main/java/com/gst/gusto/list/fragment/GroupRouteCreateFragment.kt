package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.RequestCreateRoute
import com.gst.gusto.api.RouteList
import com.gst.gusto.databinding.FragmentListGroupMRouteCreateBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter

class GroupRouteCreateFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteCreateBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    val itemList = ArrayList<MarkerItem>()
    private var tmp = 1

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

        val boardAdapter = MapRoutesAdapter(itemList,binding.lyAddRoute,null)
        boardAdapter.notifyDataSetChanged()

        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.btnPlus.setOnClickListener {
            itemList.add(MarkerItem(
                tmp.toLong(),
                tmp,0,
                37.6215001,
                127.0743010,
                binding.tvRestName.text.toString(),
                "",
                false
            ))
            tmp++
            boardAdapter.notifyItemInserted(itemList.size-1)
            if(itemList.size==6) {
                binding.lyAddRoute.visibility = View.INVISIBLE
            }
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

    }
    fun getRequestRoutesData() {
        val routeList = ArrayList<RouteList>()
        for(data in itemList) {
            routeList.add(RouteList(data.storeId,data.ordinal,null,null,null,null,null))
        }
        gustoViewModel.requestRoutesData = RequestCreateRoute(binding.etRouteName.text.toString(),gustoViewModel.currentGroupId,routeList)
    }

}