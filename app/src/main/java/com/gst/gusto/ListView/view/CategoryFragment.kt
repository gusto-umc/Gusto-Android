package com.gst.gusto.ListView.view

import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.adapter.CategoryAdapter
import com.gst.gusto.ListView.adapter.CategoryBottomSheetDialog
import com.gst.gusto.ListView.adapter.ListViewCategoryAdapter
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
        val mCategoryAdapter = CategoryAdapter(view, object : CategoryAdapter.OptionsMenuClickListener{
            override fun onOptionsMenuClicked(position: Int) {
                Toast.makeText(context , "delete clicked" , Toast.LENGTH_SHORT).show()
            }

        })

        gustoViewModel.getAllUserCategory {
                result ->
            when(result){
                0 -> {
                    mCategoryAdapter.submitList(gustoViewModel.testList)
                    mCategoryAdapter.viewModel = gustoViewModel
                    rvCategory.adapter = mCategoryAdapter
                    rvCategory.layoutManager = LinearLayoutManager(this.requireActivity())
                }
                1 -> {
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
                }
            }
        }

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