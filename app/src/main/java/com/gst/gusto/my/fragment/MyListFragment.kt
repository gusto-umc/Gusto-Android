package com.gst.gusto.my.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.adapter.ListViewCategoryAdapter
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMyListBinding

class MyListFragment : Fragment() {

    lateinit var binding: FragmentMyListBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyListBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 서버 연결 - 데이터 로드
         */
        val mCategoryAdapter = ListViewCategoryAdapter("my", requireFragmentManager(), view)

        gustoViewModel.getAllUserCategory {
            result ->
            when(result){
                0 -> {
                    mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
                    mCategoryAdapter.viewModel = gustoViewModel
                    binding.rvMyCategory.adapter = mCategoryAdapter
                    binding.rvMyCategory.layoutManager = LinearLayoutManager(this.requireActivity())
                    binding.rvMyCategory.visibility = View.VISIBLE
                }
                1 -> {
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 아이템 클릭 시 내 리뷰 화면으로 이동하기
    }

}