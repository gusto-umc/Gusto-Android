package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gst.gusto.databinding.FragmentListRouteBinding
import com.gst.gusto.databinding.FragmentMyReviewBinding

class ListRouteFragment : Fragment() {

    lateinit var binding: FragmentListRouteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteBinding.inflate(inflater, container, false)

        return binding.root

    }

}