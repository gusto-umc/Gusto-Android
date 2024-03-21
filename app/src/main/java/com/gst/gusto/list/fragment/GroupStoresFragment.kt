package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMStoresBinding
import com.gst.gusto.list.adapter.GroupAdapter
import com.gst.gusto.list.adapter.RestItem

class GroupStoresFragment : Fragment() {

    lateinit var binding: FragmentListGroupMStoresBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMStoresBinding.inflate(inflater, container, false)

        if(gustoViewModel.routeStorTmpData != null) {
            var data = gustoViewModel.routeStorTmpData
            if(gustoViewModel.groupFragment==0) {
                gustoViewModel?.addGroupStore(data!!.storeId.toLong()) { result ->
                    when(result) {
                        1 -> {
                            checkGroupStores()
                        }
                        2-> Toast.makeText(requireContext(), "이미 해당 그룹에 존재하는 식당입니다.", Toast.LENGTH_SHORT).show()
                        else -> {
                            Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }


            gustoViewModel.routeStorTmpData = null
        }

        binding.fabMain.setOnClickListener {
            (requireActivity() as MainActivity).getCon().navigate(R.id.action_groupFragment_to_routeSearchFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gustoViewModel.getGroupStores {result ->
            when(result) {
                1 -> {
                    val rv_board = binding.rv
                    var itemList = ArrayList<RestItem>()
                    itemList = gustoViewModel.storeListLiveData
                    val boardAdapter = GroupAdapter(itemList,gustoViewModel,this)
                    boardAdapter.notifyDataSetChanged()
                    rv_board.adapter = boardAdapter
                    rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                }
                else -> {
                    Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun checkGroupStores() {
        gustoViewModel.getGroupStores {result ->
            when(result) {
                1 -> {
                    val rv_board = binding.rv
                    var itemList = ArrayList<RestItem>()
                    itemList = gustoViewModel.storeListLiveData
                    val boardAdapter = GroupAdapter(itemList,gustoViewModel,this)
                    boardAdapter.notifyDataSetChanged()
                    rv_board.adapter = boardAdapter
                    rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                }
                else -> {
                    Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}