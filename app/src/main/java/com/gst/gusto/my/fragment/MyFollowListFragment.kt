package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        val howAdapter = FollowListAdapter(followList,this)
        binding.tvTitle.text = gustoViewModel.followListTitleName

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

}