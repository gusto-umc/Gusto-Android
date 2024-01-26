package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentReviewDetailEditBinding
import com.gst.gusto.review_write.adapter.ReviewHowAdapter

class ReviewDetailEditFragment : Fragment() {

    lateinit var binding: FragmentReviewDetailEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewDetailEditBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reviewDetailEdit_to_myFragment)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // 평가 리사이클러뷰
        val rv_board = binding.rvHows
        val howList = mutableListOf(3,3,3,3,3)
        val howAdapter = ReviewHowAdapter(howList,3)
        howAdapter.notifyDataSetChanged()

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }



}