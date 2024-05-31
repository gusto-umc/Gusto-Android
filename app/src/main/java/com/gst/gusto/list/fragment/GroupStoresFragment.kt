package com.gst.gusto.list.fragment

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
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMStoresBinding
import com.gst.gusto.list.adapter.GroupAdapter
import com.gst.gusto.list.adapter.RestItem

class GroupStoresFragment : Fragment() {

    lateinit var binding: FragmentListGroupMStoresBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    var hasNext = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMStoresBinding.inflate(inflater, container, false)

        binding.fabMain.setOnClickListener {
            gustoViewModel.groupFragment = 0
            (requireActivity() as MainActivity).getCon().navigate(R.id.action_groupFragment_to_routeSearchFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gustoViewModel.groupStoresFragment = this
        setGroupStores()
    }

    fun setGroupStores() {
        gustoViewModel.getGroupStores(null) {result, getHasNext ->
            when(result) {
                1 -> {
                    var itemList = listOf<RestItem>()
                    val boardAdapter = GroupAdapter(itemList.toMutableList(),gustoViewModel,this)

                    boardAdapter.notifyDataSetChanged()
                    binding.rv.adapter = boardAdapter
                    binding.rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                    boardAdapter.addItems(gustoViewModel.storeListLiveData)
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
                                gustoViewModel.getGroupStores(gustoViewModel.storeListLiveData.last().groupListId) {result, getHasNext ->
                                    hasNext = getHasNext
                                    when(result) {
                                        1 -> {
                                            val handler = Handler(Looper.getMainLooper())
                                            handler.postDelayed({
                                                boardAdapter.addItems(gustoViewModel.storeListLiveData)
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
                else -> {
                    Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}