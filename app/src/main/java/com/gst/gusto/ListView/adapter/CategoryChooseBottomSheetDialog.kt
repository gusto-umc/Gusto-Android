package com.gst.gusto.ListView.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.R

class CategoryChooseBottomSheetDialog(val itemClick : (Int) -> Unit) : BottomSheetDialogFragment() {

    private var sampleCategoryArray = arrayListOf<CategorySimple>(
        CategorySimple(id = 0, categoryName = "양식", categoryIcon = R.drawable.category_icon_1, storeCount = 3),
        CategorySimple(id = 1, categoryName = "일식", categoryIcon = R.drawable.category_icon_1, storeCount = 0),
        CategorySimple(id = 2, categoryName = "한식", categoryIcon = R.drawable.category_icon_1, storeCount = 2),
        CategorySimple(id = 3, categoryName = "중식", categoryIcon = R.drawable.category_icon_1, storeCount = 5),
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.map_choose_category_bottomsheetdialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.findViewById<ImageView>(R.id.iv_category_choose_x)?.setOnClickListener {
            itemClick(0)
            dialog?.dismiss()
        }

        //카테고리 댑터 선언 + 연결
        val dCategoryChooseAdapter = CategoryChooseAdapter()
        dCategoryChooseAdapter.setItemClickListener(object : CategoryChooseAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: CategorySimple) {
                Log.d("category choose", dataSet.categoryName)
                itemClick(1)
                dialog?.dismiss()
            }

        })
        val dChooseAdapter = CategoryChooseAdapter()
        dCategoryChooseAdapter.submitList(sampleCategoryArray)
        view?.findViewById<RecyclerView>(R.id.rv_category_choose)?.adapter = dCategoryChooseAdapter
        view?.findViewById<RecyclerView>(R.id.rv_category_choose)?.layoutManager = LinearLayoutManager(this.requireActivity())
    }
}