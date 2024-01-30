package com.gst.gusto.ListView.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.adapter.ListViewCategoryAdapter
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMapListviewBinding

class MapListViewFragment : Fragment() {

    private lateinit var binding : FragmentMapListviewBinding
    private var sampleCategoryData = arrayListOf<CategorySimple>(
        CategorySimple(0, "카페", 0, 3),
        CategorySimple(1, "한식", 0, 0)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_listview, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryRvShow = binding.rvMapListviewCategoryShow
        val categoryRvEdit = binding.rvMapListviewCategoryEdit

        /**
         * 카테고리Show 연결
         * 아이템 클릭 리스너
         */
        val cateShowAdapter = ListViewCategoryAdapter()
        cateShowAdapter.submitList(sampleCategoryData)
        categoryRvShow.adapter = cateShowAdapter
        categoryRvShow.layoutManager = LinearLayoutManager(this.requireActivity())


        /**
         * 카테고리Edit 연결
         * 체크박스 리스너 처리
         */

        /**
         * 뒤로가기 버튼, 시스템 뒤로가기 클릭리스너
         */
        binding.ivMapListviewBack.setOnClickListener {
            Toast.makeText(this.requireContext(), "뒤로가기 클릭", Toast.LENGTH_SHORT).show()
        }

        /**
         * 편집 버튼 클릭리스너
         */
        binding.tvMapListviewEdit.setOnClickListener {
            Toast.makeText(this.requireContext(), "편집 클릭", Toast.LENGTH_SHORT).show()
        }

        /**
         * 정렬 순서 클릭 리스너
         */
        binding.layoutListviewOrder.setOnClickListener {
            Toast.makeText(this.requireContext(), "정렬순서 클릭", Toast.LENGTH_SHORT).show()
        }

        /**
         * 전체선택 클릭 리스너
         */

        /**
         * 추가fab 클릭 리스너
         */
        binding.fabMapListviewAdd.setOnClickListener {
            Toast.makeText(this.requireContext(), "카테고리 추가 클릭", Toast.LENGTH_SHORT).show()
        }

        /**
         * 삭제fab 클릭 리스너
         */

    }

}