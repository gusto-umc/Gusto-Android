package com.gst.clock.Fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.createUpdateProgressRunnable
import com.gst.gusto.databinding.FragmentReviewAdd6Binding
import com.gst.gusto.review_write.adapter.HowItem
import com.gst.gusto.review_write.adapter.ReviewHowAdapter

class ReviewAdd6Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd6Binding
    lateinit var progressBar : ProgressBar
    private val menuList = ArrayList<EditText>()
    private val handler = Handler()
    private val progressPoint = 500
    private val howList = mutableListOf(3,3,3,3,3)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd6Binding.inflate(inflater, container, false)

        val bundle = Bundle().apply {
            putInt("progress", progressPoint)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd6Fragment_to_reviewAdd5Fragment,bundle)
        }
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd6Fragment_to_reviewAdd7Fragment,bundle)
            Log.d("howList",howList.toString())
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.pBar
        progressBar.progress = arguments?.getInt("progress", progressPoint)!!
        val updateProgressRunnable = createUpdateProgressRunnable(progressBar, progressPoint,handler)
        // 올릴 때 마다 부드럽게 움직이도록 시작
        handler.post(updateProgressRunnable)

        val rv_board = binding.rvHows
        val howAdapter = ReviewHowAdapter(howList,0)
        howAdapter.notifyDataSetChanged()

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
}
