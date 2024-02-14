package com.gst.gusto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.MapRecyclerAdapter
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentAreaBinding

class AreaFragment : Fragment() {
    lateinit var binding: FragmentAreaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_area, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_no_visited_rest)
        val recyclerView2: RecyclerView = view.findViewById(R.id.recyclerView_visited_rest)
        val recyclerView3: RecyclerView = view.findViewById(R.id.recyclerView_age_no_visited_rest)
        val adapter = MapRecyclerAdapter()
        val adapter2 = MapRecyclerAdapter()
        val adapter3 = MapRecyclerAdapter()
        recyclerView.adapter = adapter

        // 그 외의 초기화 작업 수행

        return view
    }
}
