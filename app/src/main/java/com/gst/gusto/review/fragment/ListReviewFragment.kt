package com.gst.gusto.review.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gst.gusto.databinding.FragmentListReviewBinding

class ListReviewFragment : Fragment() {

    lateinit var binding: FragmentListReviewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentListReviewBinding.inflate(inflater, container, false)

        return binding.root
    }

}