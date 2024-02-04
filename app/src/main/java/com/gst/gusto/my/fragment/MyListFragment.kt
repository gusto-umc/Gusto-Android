package com.gst.gusto.my.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gst.gusto.databinding.FragmentMyListBinding

class MyListFragment : Fragment() {

    lateinit var binding: FragmentMyListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyListBinding.inflate(inflater, container, false)

        return binding.root

    }

}