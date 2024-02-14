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
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseAllCategory

class CategoryChooseBottomSheetDialog(val itemClick : (Int) -> Unit) : BottomSheetDialogFragment() {

    var viewModel : GustoViewModel? = null

    private var categoryArray : List<ResponseAllCategory>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.map_choose_category_bottomsheetdialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //X 클릭리스너
        view?.findViewById<ImageView>(R.id.iv_category_choose_x)?.setOnClickListener {
            itemClick(0)
            dialog?.dismiss()
        }

        //데이터 연결
        categoryArray = viewModel!!.myAllCategoryList


        //카테고리 댑터 선언 + 연결
        val dCategoryChooseAdapter = CategoryChooseAdapter()
        dCategoryChooseAdapter.setItemClickListener(object : CategoryChooseAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: ResponseAllCategory) {
                //서버 연결 (찜)
                val storeId = 4 // 혜성 카레
                viewModel!!.addPin(dataSet.myCategoryId.toLong(), storeId.toLong()){
                    result ->
                    when(result){
                        0 -> {
                            //성공
                            itemClick(1)
                            dialog?.dismiss()
                        }
                        1 -> {
                            //실패
                            itemClick(0)
                            dialog?.dismiss()
                        }

                    }
                }

            }

        })
        val dChooseAdapter = CategoryChooseAdapter()
        dCategoryChooseAdapter.submitList(categoryArray)
        view?.findViewById<RecyclerView>(R.id.rv_category_choose)?.adapter = dCategoryChooseAdapter
        view?.findViewById<RecyclerView>(R.id.rv_category_choose)?.layoutManager = LinearLayoutManager(this.requireActivity())
    }
}