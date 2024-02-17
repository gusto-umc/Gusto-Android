package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMRouteRoutesBinding
import com.gst.gusto.list.adapter.GroupAdapter
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class GroupRouteRoutesFragment : Fragment() {

    lateinit var binding: FragmentListGroupMRouteRoutesBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    var itemList = ArrayList<GroupItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMRouteRoutesBinding.inflate(inflater, container, false)

        binding.fabMain.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_groupMRSFragment_to_groupMRCFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gustoViewModel.getGroupRoutes {result, data ->
            when(result) {
                1 -> {
                    if(data !=null) {
                        val rv_board = binding.rv
                        itemList = data
                        val boardAdapter = LisAdapter(itemList,null,2,gustoViewModel,this)
                        boardAdapter.notifyDataSetChanged()

                        rv_board.adapter = boardAdapter
                        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    }
                }
            }
        }
    }
    fun checkRoutes() {
        gustoViewModel.getGroupRoutes {result, data ->
            when(result) {
                1 -> {
                    if(data !=null) {
                        val rv_board = binding.rv
                        itemList = data
                        val boardAdapter = LisAdapter(itemList, null, 2, gustoViewModel,this)
                        boardAdapter.notifyDataSetChanged()

                        rv_board.adapter = boardAdapter
                        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    }
                } else -> {
                Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
            }
            }
        }
    }
}