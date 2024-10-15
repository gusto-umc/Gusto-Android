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
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.gst.gusto.MainActivity
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupBinding
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class ListGroupFragment : Fragment() {

    lateinit var binding: FragmentListGroupBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private var hasNext:Boolean = false
    fun callActivityFunction(): NavController {
        return (activity as? MainActivity)?.getCon() ?: throw IllegalStateException("NavController is null")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupBinding.inflate(inflater, container, false)


        binding.bannerAd.loadAd(AdRequest.Builder().build())

        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    override fun onResume() {
        super.onResume()
        gustoViewModel.listFragment = "group"

        checkGroups()
    }
    fun checkGroups() {
        gustoViewModel.getGroups(null) {result, getHasNext ->
            when(result) {
                1 -> {
                    var itemList : List<GroupItem> = listOf()
                    val rv_board = binding.rvListGourp
                    itemList = gustoViewModel.myGroupList
                    val boardAdapter = LisAdapter(itemList.toMutableList(), callActivityFunction(), 0, gustoViewModel,null)
                    boardAdapter.notifyDataSetChanged()
                    rv_board.adapter = boardAdapter
                    rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    hasNext = getHasNext
                    if(hasNext) boardAdapter.addLoading()
                    binding.rvListGourp.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                            val rvPosition =
                                (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                            // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                            val totalCount =
                                recyclerView.adapter?.itemCount?.minus(1)

                            // 페이징 처리
                            if(rvPosition == totalCount&&hasNext) {
                                gustoViewModel.getGroups(gustoViewModel.myGroupList.last().groupId) {result, getHasNext ->
                                    hasNext = getHasNext
                                    when(result) {
                                        1 -> {
                                            val handler = Handler(Looper.getMainLooper())
                                            handler.postDelayed({
                                                boardAdapter.addItems(gustoViewModel.myGroupList)
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