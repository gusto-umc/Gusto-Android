package com.gst.gusto.list.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    var hasNext = false

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

        checkRoutes()
    }
    fun checkRoutes() {
        gustoViewModel.getGroupRoutes(null) {result, data, getHasNext ->
            when(result) {
                1 -> {
                    if(data !=null) {
                        var itemList:List<GroupItem> = listOf()
                        val boardAdapter = LisAdapter(itemList.toMutableList(), null, 2, gustoViewModel,this)
                        boardAdapter.notifyDataSetChanged()

                        binding.rv.adapter = boardAdapter
                        binding.rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                        boardAdapter.addItems(data)
                        hasNext = getHasNext
                        if(!hasNext) boardAdapter.removeLastItem()

                        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                                // 페이징 처리
                                if(rvPosition == totalCount&&hasNext) {
                                    gustoViewModel.getGroupRoutes(data.last().groupId) {result, data2,getHasNext ->
                                        hasNext = getHasNext
                                        when(result) {
                                            1 -> {
                                                val handler = Handler(Looper.getMainLooper())
                                                handler.postDelayed({
                                                    if(data2!=null)
                                                        boardAdapter.addItems(data2)
                                                    if(!hasNext) boardAdapter.removeLastItem()
                                                }, 1000)

                                            }
                                            else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        })
                    }
                } else -> {
                Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
            }
            }
        }
    }
}