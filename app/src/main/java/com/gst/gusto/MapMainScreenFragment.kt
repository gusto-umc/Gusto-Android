package com.gst.gusto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gst.gusto.databinding.FragmentMapMainScreenBinding

class MapMainScreenFragment : Fragment() {

    // Binding 추가
    private lateinit var binding: FragmentMapMainScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Binding 초기화
        binding = FragmentMapMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SearchView 참조
        val searchView = binding.search

        // SearchView에 리스너 추가


    }
}
