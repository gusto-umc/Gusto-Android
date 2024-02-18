package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class ListGroupFragment : Fragment() {

    lateinit var binding: FragmentListGroupBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupBinding.inflate(inflater, container, false)

        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    override fun onResume() {
        super.onResume()
        gustoViewModel.listFragment = "group"
        fun callActivityFunction(): NavController {
            return (activity as? MainActivity)?.getCon() ?: throw IllegalStateException("NavController is null")
        }

        gustoViewModel.getGroups {result ->
            when(result) {
                1 -> {
                    var itemList = ArrayList<GroupItem>()
                    val rv_board = binding.rvListGourp
                    itemList = gustoViewModel.myGroupList
                    val boardAdapter = LisAdapter(itemList, callActivityFunction(), 0, gustoViewModel,null)
                    boardAdapter.notifyDataSetChanged()
                    rv_board.adapter = boardAdapter
                    rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                } else -> {
                    Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun checkGroups() {
        gustoViewModel.getGroups {result ->
            when(result) {
                1 -> {
                    var itemList = ArrayList<GroupItem>()
                    val rv_board = binding.rvListGourp
                    itemList = gustoViewModel.myGroupList
                    val boardAdapter = LisAdapter(itemList, (requireActivity() as MainActivity).getCon(), 0, gustoViewModel,null)
                    boardAdapter.notifyDataSetChanged()
                    rv_board.adapter = boardAdapter
                    rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                } else -> {
                    Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}