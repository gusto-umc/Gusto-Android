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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.gst.gusto.MainActivity
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListRouteBinding
import com.gst.gusto.list.ListFragment
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter

class ListRouteFragment : Fragment() {

    lateinit var binding: FragmentListRouteBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    var hasNext = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteBinding.inflate(inflater, container, false)

        binding.bannerAd.loadAd(AdRequest.Builder().build())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        gustoViewModel.listFragment = "route"
        checkRoutes()
    }

    override fun onDestroy() {
        super.onDestroy()
        val frag = requireParentFragment().parentFragment as ListFragment
        frag.callBtnGroup()
    }
    fun checkRoutes() {
        gustoViewModel.getMyRoute(null) {result, getHasNext ->
            when(result) {
                1 -> {
                    var itemList:List<GroupItem> = listOf()
                    val boardAdapter = LisAdapter(itemList.toMutableList(), (requireActivity() as MainActivity).getCon(), 1, gustoViewModel,this)
                    boardAdapter.notifyDataSetChanged()

                    binding.rvListRoute.adapter = boardAdapter
                    binding.rvListRoute.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                    boardAdapter.addItems(gustoViewModel.myRouteList)
                    hasNext = getHasNext
                    if(!hasNext) boardAdapter.removeLastItem()
                    binding.rvListRoute.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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