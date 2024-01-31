package com.gst.gusto.review.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gst.gusto.databinding.FragmentCalendarReviewBinding


class CalendarReviewFragment : Fragment() {

    lateinit var binding: FragmentCalendarReviewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarReviewBinding.inflate(inflater, container, false)

        return binding.root
    }
}