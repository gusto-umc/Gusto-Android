package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMStoresBinding
import com.gst.gusto.list.adapter.GroupAdapter
import com.gst.gusto.list.adapter.LisAdapter
import com.gst.gusto.list.adapter.RestItem

class GroupStoresFragment : Fragment() {

    lateinit var binding: FragmentListGroupMStoresBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMStoresBinding.inflate(inflater, container, false)

        binding.fabMain.setOnClickListener {
            gustoViewModel.addGroupStore(2) {result ->
                when(result) {
                    1 -> {

                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = binding.rv

        var itemList = ArrayList<RestItem>()

        gustoViewModel.getGroupStores {result ->
            when(result) {
                1 -> {
                    itemList = gustoViewModel.storeListLiveData
                    val boardAdapter = GroupAdapter(itemList)
                    boardAdapter.notifyDataSetChanged()
                    rv_board.adapter = boardAdapter
                    rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                }
            }
        }

    }

}