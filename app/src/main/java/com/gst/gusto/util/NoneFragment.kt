package com.gst.gusto.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gst.gusto.databinding.FragmentNoneBinding


class NoneFragment : Fragment() {
    lateinit var binding: FragmentNoneBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoneBinding.inflate(inflater, container, false)



        return binding.root
    }
}