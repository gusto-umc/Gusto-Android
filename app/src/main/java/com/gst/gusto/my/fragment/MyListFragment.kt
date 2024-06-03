package com.gst.gusto.my.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.adapter.CategoryAdapter
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
        if(gustoViewModel.currentFeedNickname == gustoViewModel.userNickname){
            //my
            gustoViewModel.myAllCategoryList.clear()

            val mCategoryAdapter = CategoryAdapter(view, "my")
            mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
            mCategoryAdapter.viewModel = gustoViewModel
            mCategoryAdapter.mContext = context
            binding.rvMyCategory.adapter = mCategoryAdapter
            binding.rvMyCategory.layoutManager = LinearLayoutManager(this.requireActivity())

            var hasNext = false

            gustoViewModel.getPPMyCategory(null){
                    result, getHasNext ->
                when(result){
                    1 -> {
                        //success
                        mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
                        hasNext = getHasNext
                        mCategoryAdapter.notifyDataSetChanged()
                    }
                    else-> {
                        Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            binding.rvMyCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                    // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                    val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                    // 페이징 처리
                    if(rvPosition == totalCount && hasNext) {
                        gustoViewModel.getPPMyCategory(gustoViewModel.myAllCategoryList.last().myCategoryId) {result, getHasNext ->
                            hasNext = getHasNext
                            when(result) {
                                1 -> {
                                    val handler = Handler(Looper.getMainLooper())
                                    handler.postDelayed({
                                        mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
                                        mCategoryAdapter.notifyDataSetChanged()
                                    }, 1000)

                                }
                                else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            })


        }
        else {
            //other(feed)
            gustoViewModel.myAllCategoryList.clear()

            val mCategoryAdapter = CategoryAdapter(view, "my")
            mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
            mCategoryAdapter.viewModel = gustoViewModel
            mCategoryAdapter.mContext = context
            binding.rvMyCategory.adapter = mCategoryAdapter
            binding.rvMyCategory.layoutManager = LinearLayoutManager(this.requireActivity())

            var hasNext = false

            gustoViewModel.getPPMyCategory(null){
                    result, getHasNext ->
                when(result){
                    1 -> {
                        //success
                        mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
                        hasNext = getHasNext
                        mCategoryAdapter.notifyDataSetChanged()
                    }
                    else-> {
                        Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            binding.rvMyCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                    // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                    val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                    // 페이징 처리
                    if(rvPosition == totalCount && hasNext) {
                        gustoViewModel.getPPMyCategory(gustoViewModel.myAllCategoryList.last().myCategoryId) {result, getHasNext ->
                            hasNext = getHasNext
                            when(result) {
                                1 -> {
                                    val handler = Handler(Looper.getMainLooper())
                                    handler.postDelayed({
                                        mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
                                        mCategoryAdapter.notifyDataSetChanged()
                                    }, 1000)

                                }
                                else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            })
        }
    }

}