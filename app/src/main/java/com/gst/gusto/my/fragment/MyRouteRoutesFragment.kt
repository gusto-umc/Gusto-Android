package com.gst.gusto.my

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.MainActivity
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMyRouteRoutesBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class MyRouteRoutesFragment : Fragment() {

    lateinit var binding: FragmentMyRouteRoutesBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    var hasNext = false
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

        var itemList:List<GroupItem> = listOf()
        val rv_board = binding.recyclerView

        if(nickname!="") {
            gustoViewModel.getOtherRoute(null,nickname) {result,getHasNext ->
                when(result) {
                    1 -> {
                        val boardAdapter = LisAdapter(itemList.toMutableList(), null, 3, gustoViewModel,null)
                        boardAdapter.notifyDataSetChanged()
                        rv_board.adapter = boardAdapter
                        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                        boardAdapter.addItems(gustoViewModel.otherRouteList)
                        hasNext = getHasNext
                        if(!hasNext) boardAdapter.removeLastItem()
                        rv_board.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                                // 페이징 처리
                                if(rvPosition == totalCount&&hasNext) {
                                    gustoViewModel.getOtherRoute(gustoViewModel.otherRouteList.last().groupId,nickname) {result, getHasNext ->
                                        hasNext = getHasNext
                                        when(result) {
                                            1 -> {
                                                val handler = Handler(Looper.getMainLooper())
                                                handler.postDelayed({
                                                    boardAdapter.addItems(gustoViewModel.otherRouteList)
                                                    if(!hasNext) boardAdapter.removeLastItem()
                                                }, 1000)

                                            }
                                            else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        })
                    } else -> {
                        Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            gustoViewModel.getMyRoute(null) {result, getHasNext ->
                when(result) {
                    1 -> {
                        val boardAdapter = LisAdapter(itemList.toMutableList(), null, 3, gustoViewModel,null)
                        boardAdapter.notifyDataSetChanged()
                        rv_board.adapter = boardAdapter
                        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                        boardAdapter.addItems(gustoViewModel.myRouteList)
                        hasNext = getHasNext
                        if(!hasNext) boardAdapter.removeLastItem()

                        rv_board.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                                // 페이징 처리
                                if(rvPosition == totalCount&&hasNext) {
                                    gustoViewModel.getMyRoute(gustoViewModel.myRouteList.last().groupId) {result, getHasNext ->
                                        hasNext = getHasNext
                                        when(result) {
                                            1 -> {
                                                val handler = Handler(Looper.getMainLooper())
                                                handler.postDelayed({
                                                    boardAdapter.addItems(gustoViewModel.myRouteList)
                                                    if(!hasNext) boardAdapter.removeLastItem()
                                                }, 1000)

                                            }
                                            else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        })
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