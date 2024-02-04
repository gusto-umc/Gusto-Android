package com.gst.gusto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색 버튼이 눌렸을 때 처리하는 부분
                // query 변수에 검색어가 전달
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색어가 변경될 때 처리하는 부분
                // newText 변수에 현재 입력된 검색어 전달
                return true
            }
        })

    }
}
