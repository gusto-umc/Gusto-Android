package com.gst.clock.Fragment

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.createUpdateProgressRunnable
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.databinding.FragmentReviewAdd7Binding

class ReviewAdd7Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd7Binding
    lateinit var progressBar : ProgressBar
    private val menuList = ArrayList<EditText>()
    private val handler = Handler()
    private val progressPoint = 600

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd7Binding.inflate(inflater, container, false)

        val bundle = Bundle().apply {
            putInt("progress", progressPoint)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd7Fragment_to_reviewAdd6Fragment,bundle)
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

    }

}
