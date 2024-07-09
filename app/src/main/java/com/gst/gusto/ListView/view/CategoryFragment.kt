package com.gst.gusto.ListView.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.adapter.CategoryAdapter
import com.gst.gusto.ListView.adapter.CategoryBottomSheetDialog
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentCategoryBinding


class CategoryFragment : Fragment() {

    private lateinit var binding : FragmentCategoryBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCategory = binding.rvCategory
        val fabAdd = binding.fabCategoryAdd

        /**
         * 1. 서버 연결, rv 연결
         */

        gustoViewModel.myAllCategoryList.clear()

        val mCategoryAdapter = CategoryAdapter(view, "map", requireFragmentManager())
        mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
        mCategoryAdapter.viewModel = gustoViewModel
        mCategoryAdapter.mContext = context
        rvCategory.adapter = mCategoryAdapter
        rvCategory.layoutManager = LinearLayoutManager(this.requireActivity())

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

        rvCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        /**
         * 2. 뒤로가기 클릭 리스너
         */
        binding.ivCategoryBack.setOnClickListener {
            findNavController().popBackStack()
        }

        /**
         * 3. 카테고리 추가
         */
        fabAdd.setOnClickListener{
            val categoryAddBottomSheetDialog = CategoryBottomSheetDialog(){
                when(it){
                    0 -> {
                        //추가 성공
                        //카테고리 새로 받아와서 연결시키기
                        //getMapCategories()
                    }
                    1 -> {
                        //추가 실페
                        Toast.makeText(context, "추가 fail", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            categoryAddBottomSheetDialog.isAdd = true
            categoryAddBottomSheetDialog.categoryEdiBottomSheetData = null
            categoryAddBottomSheetDialog.viewModel = gustoViewModel
            categoryAddBottomSheetDialog.show(requireFragmentManager(), categoryAddBottomSheetDialog.tag)
        }

    }

}