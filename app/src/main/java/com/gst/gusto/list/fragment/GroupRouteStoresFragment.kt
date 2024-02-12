package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMRouteStoresBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RouteAdapter

class GroupRouteStoresFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteStoresBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteStoresBinding.inflate(inflater, container, false)

        val bundle = Bundle()
        binding.fabEdit.setOnClickListener {
            fun callActivityFunction(): NavController {
                return (activity as? MainActivity)?.getCon() ?: throw IllegalStateException("NavController is null")
            }
            bundle.putBoolean("edit",true)
            callActivityFunction().navigate(R.id.action_groupFragment_to_groupMRoutMapFragment,bundle)
        }
        binding.fabMap.setOnClickListener {
            fun callActivityFunction(): NavController {
                return (activity as? MainActivity)?.getCon() ?: throw IllegalStateException("NavController is null")
            }
            callActivityFunction().navigate(R.id.action_groupFragment_to_groupMRoutMapFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = binding.rv
        val itemList = gustoViewModel.markerListLiveData.value!!
        val boardAdapter = RouteAdapter(itemList,requireActivity() as MainActivity)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

}