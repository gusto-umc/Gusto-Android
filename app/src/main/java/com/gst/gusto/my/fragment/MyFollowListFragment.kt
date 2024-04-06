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
import com.gst.gusto.my.adapter.FollowListAdapter

class MyFollowListFragment() : Fragment() {

    lateinit var binding: FragmentMyFollowListBinding
    private var followList: List<Member> = listOf()
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
        //임시 데이터
        followList = gustoViewModel.followList

        val rv_board = binding.rvFollowList
        val howAdapter = FollowListAdapter(followList.toMutableList(),this)
        binding.tvTitle.text = gustoViewModel.followListTitleName

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFollowList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val rvPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                val totalCount =
                    recyclerView.adapter?.itemCount?.minus(1)
                Log.d("viewmodelHELPHELP","${followList.last().followId}, ${followList}")

                // 페이징 처리
                if(rvPosition == totalCount&&binding.progressBar.visibility==View.VISIBLE) {
                    if(binding.tvTitle.text.toString() == "팔로워") {
                        gustoViewModel.getFollowerP(followList.last().followId) {result, followListP ->
                            when(result) {
                                1 -> {
                                    val handler = Handler(Looper.getMainLooper())
                                    handler.postDelayed({
                                        if (followListP != null) {
                                            followList = followListP
                                            howAdapter.addItems(followListP)
                                        }
                                    }, 1000)

                                }
                                2-> {
                                    binding.progressBar.visibility= View.GONE
                                }
                                3 -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else if(binding.tvTitle.text.toString() == "팔로잉 중"){
                        gustoViewModel.getFollowingP(followList.last().followId) {result, followListP ->
                            when(result) {
                                1 -> {
                                    val handler = Handler(Looper.getMainLooper())
                                    handler.postDelayed({
                                        if (followListP != null) {
                                            followList = followListP
                                            howAdapter.addItems(followListP)
                                        }
                                    }, 1000)

                                }
                                2-> {
                                    binding.progressBar.visibility= View.GONE
                                }
                                3 -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })

    }

}