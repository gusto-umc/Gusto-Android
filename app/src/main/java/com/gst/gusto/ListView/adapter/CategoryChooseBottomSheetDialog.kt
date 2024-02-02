package com.gst.gusto.ListView.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.R

class CategoryChooseBottomSheetDialog(val itemClick : (Int) -> Unit) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.map_choose_category_bottomsheetdialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.findViewById<RecyclerView>(R.id.rv_category_choose)?.setOnClickListener {
            itemClick(0)
            dialog?.dismiss()
        }
        view?.findViewById<ImageView>(R.id.iv_category_choose_x)?.setOnClickListener {
            itemClick(0)
            dialog?.dismiss()
        }

        //카테고리 댑터 선언 + 연결
        view?.findViewById<RecyclerView>(R.id.rv_category_choose)?.adapter
    }
}