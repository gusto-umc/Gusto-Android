package com.gst.clock.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.Member
import com.gst.gusto.databinding.FragmentMyFollowListBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.my.adapter.FollowListAdapter

class MyFollowListFragment() : Fragment() {

    lateinit var binding: FragmentMyFollowListBinding
    val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyFollowListBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = gustoViewModel.followListTitleName
        var option = 0
        if(binding.tvTitle.text == "팔로워") option = 1
        else if(binding.tvTitle.text =="팔로잉") option = 0
        else if(binding.tvTitle.text == "그룹 리스트&루트") option = 2
        var hasNext = false

        var itemList : List<Member> = listOf()
        val rv_board = binding.rvFollowList
        val howAdapter = FollowListAdapter(itemList.toMutableList(),this)

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        if(option<2) {
            gustoViewModel.getFollow(null,option) {result, getHasNext ->
                when(result) {
                    1 -> {
                        howAdapter.addItems(gustoViewModel.followList)
                        hasNext = getHasNext
                        if(!hasNext) howAdapter.removeLastItem()

                        binding.rvFollowList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                                // 페이징 처리
                                if(rvPosition == totalCount&&hasNext) {
                                    gustoViewModel.getFollow(gustoViewModel.followList.last().followId,option) {result, getHasNext ->
                                        hasNext = getHasNext
                                        when(result) {
                                            1 -> {
                                                val handler = Handler(Looper.getMainLooper())
                                                handler.postDelayed({
                                                    howAdapter.addItems(gustoViewModel.followList)
                                                    if(!hasNext) howAdapter.removeLastItem()
                                                }, 1000)

                                            }
                                            else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        })

                    }
                    else-> {
                        Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if(option==2){
            gustoViewModel.getGroupMembers(null) {result, getHasNext ->
                when(result) {
                    1 -> {
                        howAdapter.addItems(gustoViewModel.followList)
                        hasNext = getHasNext
                        if(!hasNext) howAdapter.removeLastItem()

                        binding.rvFollowList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                                // 페이징 처리
                                if(rvPosition == totalCount&&hasNext) {
                                    gustoViewModel.getGroupMembers(gustoViewModel.followList.last().groupMemberId) {result, getHasNext ->
                                        hasNext = getHasNext
                                        when(result) {
                                            1 -> {
                                                val handler = Handler(Looper.getMainLooper())
                                                handler.postDelayed({
                                                    howAdapter.addItems(gustoViewModel.followList)
                                                    if(!hasNext) howAdapter.removeLastItem()
                                                }, 1000)

                                            }
                                            else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        })
                    }
                    else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

}