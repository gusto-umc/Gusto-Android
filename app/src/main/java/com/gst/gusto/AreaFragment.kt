package com.gst.clock.Fragment

import MapRecyclerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentAreaBinding

class AreaFragment : Fragment() {
    lateinit var binding: FragmentAreaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("log_test","제박..")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAreaBinding.inflate(inflater, container, false)
        val view = binding.root



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val layoutManager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val layoutManager3 = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_no_visited_rest)
        val recyclerView2: RecyclerView = view.findViewById(R.id.recyclerView_visited_rest)
        val recyclerView3: RecyclerView = view.findViewById(R.id.recyclerView_age_no_visited_rest)

        recyclerView.layoutManager = layoutManager
        recyclerView2.layoutManager = layoutManager2
        recyclerView3.layoutManager = layoutManager3

        // 아이템 담기
        val itemList = ArrayList<String>()

        //val imageResource = R.drawable.visit // 이미지 리소스 ID 가져오기
        val imageResource = "https://www.urbanbrush.net/web/wp-content/uploads/edd/2023/02/urban-20230228092421948485.jpg"
        itemList.add(imageResource)
        itemList.add(imageResource)
        itemList.add(imageResource)

        val adapter = MapRecyclerAdapter(itemList)
        val adapter2 = MapRecyclerAdapter(itemList)
        val adapter3 = MapRecyclerAdapter(itemList)

        recyclerView.adapter = adapter
        recyclerView2.adapter = adapter2
        recyclerView3.adapter = adapter3
        Log.d("log_test","보자")

    }
}
