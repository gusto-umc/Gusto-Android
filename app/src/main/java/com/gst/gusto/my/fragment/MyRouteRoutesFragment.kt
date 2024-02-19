package com.gst.gusto.my

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMyRouteRoutesBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class MyRouteRoutesFragment : Fragment() {

    lateinit var binding: FragmentMyRouteRoutesBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var itemList : ArrayList<GroupItem>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRouteRoutesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("viewmodel","help")
        val nickname = gustoViewModel.profileNickname
        if(nickname!="") {
            gustoViewModel.getOtherRoute(nickname) {result ->
                when(result) {
                    1 -> {
                        val rv_board = binding.recyclerView
                        itemList = gustoViewModel.otherRouteList
                        val boardAdapter = LisAdapter(itemList, null, 3, gustoViewModel,null)
                        boardAdapter.notifyDataSetChanged()
                        rv_board.adapter = boardAdapter
                        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    } else -> {
                    Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
                }
            }
        } else {
            gustoViewModel.getMyRoute {result ->
                when(result) {
                    1 -> {
                        val rv_board = binding.recyclerView
                        itemList = gustoViewModel.myRouteList
                        val boardAdapter = LisAdapter(itemList, null, 3, gustoViewModel,null)
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

    override fun onResume() {
        super.onResume()
        // 번들이 null이 아닌지 확인하고 "nickname" 키로 값을 가져옴
    }
}