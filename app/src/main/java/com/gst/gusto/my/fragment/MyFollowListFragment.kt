package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMyFollowListBinding
import com.gst.gusto.databinding.FragmentMyListBinding
import com.gst.gusto.my.adapter.FollowItem
import com.gst.gusto.my.adapter.FollowListAdapter
import com.gst.gusto.review_write.adapter.HowItem
import com.gst.gusto.review_write.adapter.ReviewHowAdapter

class MyFollowListFragment : Fragment() {

    lateinit var binding: FragmentMyFollowListBinding
    private val followList = ArrayList<FollowItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyFollowListBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_followList_to_myFragment)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //임시 데이터
        followList.add(FollowItem("","구스토1",))
        followList.add(FollowItem("","구스토2",))
        followList.add(FollowItem("","구스토3",))
        followList.add(FollowItem("","구스토4",))


        val rv_board = binding.rvFollowList
        val howAdapter = FollowListAdapter(followList)

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

}