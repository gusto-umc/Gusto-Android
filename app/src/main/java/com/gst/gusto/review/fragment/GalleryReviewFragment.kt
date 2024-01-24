package com.gst.gusto.review.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gst.gusto.databinding.FragmentGalleryReviewBinding

class GalleryReviewFragment : Fragment() {

    lateinit var binding: FragmentGalleryReviewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryReviewBinding.inflate(inflater, container, false)

        return binding.root
    }

}