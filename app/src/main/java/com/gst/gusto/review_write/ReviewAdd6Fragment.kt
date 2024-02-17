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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.createUpdateProgressRunnable
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewAdd6Binding
import com.gst.gusto.review_write.adapter.HowItem
import com.gst.gusto.review_write.adapter.ReviewHowAdapter

class ReviewAdd6Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd6Binding
    private val handler = Handler()
    private val progressPoint = 500
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private val howList = mutableListOf(3,3,3,3,3)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd6Binding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnNext.setOnClickListener {
            gustoViewModel.taste = howList[0]
            gustoViewModel.spiciness = howList[1]
            gustoViewModel.mood = howList[2]
            gustoViewModel.toilet = howList[3]
            gustoViewModel.parking = howList[4]
            findNavController().navigate(R.id.action_reviewAdd6Fragment_to_reviewAdd7Fragment)
            Log.d("howList",howList.toString())
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = binding.rvHows
        val howAdapter = ReviewHowAdapter(howList,0)
        howAdapter.notifyDataSetChanged()

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
    override fun onPause() {
        super.onPause()
        gustoViewModel.progress = progressPoint
    }

    override fun onResume() {
        super.onResume()
        binding.pBar.progress = gustoViewModel.progress
        val updateProgressRunnable = util.createUpdateProgressRunnable(binding.pBar, progressPoint, handler)
        handler.post(updateProgressRunnable)
    }
}
