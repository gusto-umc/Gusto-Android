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
import com.gst.gusto.my.adapter.myRouteAdapter

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

        Handler(Looper.getMainLooper()).postDelayed({
            if (nickname != "") {
                gustoViewModel.getOtherRoute(null, nickname) { result, getHasNext ->
                    when (result) {
                        1 -> {
                            activity?.let { act ->
                                val boardAdapter = myRouteAdapter(itemList.toMutableList(), gustoViewModel, act)
                                boardAdapter.notifyDataSetChanged()
                                rv_board.adapter = boardAdapter
                                rv_board.layoutManager = LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false)

                                boardAdapter.addItems(gustoViewModel.otherRouteList)
                                hasNext = getHasNext
                                if (!hasNext) boardAdapter.removeLastItem()

                                rv_board.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)
                                        val rvPosition =
                                            (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                                        val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                                        if (rvPosition == totalCount && hasNext) {
                                            gustoViewModel.getOtherRoute(
                                                gustoViewModel.otherRouteList.last().groupId,
                                                nickname
                                            ) { result, getHasNext ->
                                                hasNext = getHasNext
                                                if (result == 1) {
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        boardAdapter.addItems(gustoViewModel.otherRouteList)
                                                        if (!hasNext) boardAdapter.removeLastItem()
                                                    }, 1000)
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            } else {
                gustoViewModel.getMyRoute(null) { result, getHasNext ->
                    when (result) {
                        1 -> {
                            activity?.let { act ->
                                val boardAdapter = myRouteAdapter(itemList.toMutableList(), gustoViewModel, act)
                                boardAdapter.notifyDataSetChanged()
                                rv_board.adapter = boardAdapter
                                rv_board.layoutManager = LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false)

                                boardAdapter.addItems(gustoViewModel.myRouteList)
                                hasNext = getHasNext
                                if (!hasNext) boardAdapter.removeLastItem()

                                rv_board.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)
                                        val rvPosition =
                                            (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                                        val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                                        if (rvPosition == totalCount && hasNext) {
                                            gustoViewModel.getMyRoute(gustoViewModel.myRouteList.last().groupId) { result, getHasNext ->
                                                hasNext = getHasNext
                                                if (result == 1) {
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        boardAdapter.addItems(gustoViewModel.myRouteList)
                                                        if (!hasNext) boardAdapter.removeLastItem()
                                                    }, 1000)
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }, 100) // 🔥 0.1초(100ms) 뒤에 실행

    }

    override fun onResume() {
        super.onResume()
        // 번들이 null이 아닌지 확인하고 "nickname" 키로 값을 가져옴
    }
}