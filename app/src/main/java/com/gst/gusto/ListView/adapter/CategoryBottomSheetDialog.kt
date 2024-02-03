package com.gst.gusto.ListView.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.ListView.Model.CategoryDetail
import com.gst.gusto.R

class CategoryBottomSheetDialog(val itemClick : (Int) -> Unit) : BottomSheetDialogFragment() {

    var isAdd = false
    var categoryEdiBottomSheetData : CategoryDetail? = null
    private val sampleIconArray = arrayListOf<Int>(
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1,
        R.drawable.category_icon_1
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.map_listview_bottomsheetdialog_add, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.findViewById<TextView>(R.id.tv_category_save)?.setOnClickListener {
            itemClick(0)
            dialog?.dismiss()
        }
        view?.findViewById<ImageView>(R.id.iv_bottomsheet_category_x)?.setOnClickListener {
            dialog?.dismiss()
        }

        view?.findViewById<TextView>(R.id.tv_category_edit)?.setOnClickListener {
            view?.findViewById<ImageView>(R.id.iv_bottomsheet_category_x)?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.tv_category_edit)?.visibility = View.GONE
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_title)?.isEnabled = true
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_desc)?.isEnabled = true
            // 아이콘 클릭리스너
            //icon click listener
            view?.findViewById<ImageView>(R.id.iv_category_add_icon)?.setOnClickListener {
                if(view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.isVisible){
                    view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.GONE
                }
                else{
                    view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.VISIBLE
                }
            }
            view?.findViewById<Switch>(R.id.switch_category_public)?.isEnabled = true
            //스위치 클릭 리스너
        }

        if(isAdd){
            //카테고리 추가 상황 시
            //icon click listener
            view?.findViewById<ImageView>(R.id.iv_category_add_icon)?.setOnClickListener {
                if(view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.isVisible){
                    view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.GONE
                }
                else{
                    view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.VISIBLE
                }
            }
            view?.findViewById<Switch>(R.id.switch_category_public)?.isEnabled = true

        }
        else{
            view?.findViewById<ImageView>(R.id.iv_bottomsheet_category_x)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.tv_category_edit)?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.tv_category_bottomsheet_banner)?.text = "카테고리 수정하기"
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_title)?.setText(categoryEdiBottomSheetData?.categoryName)
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_title)?.isEnabled = false
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_desc)?.setText(categoryEdiBottomSheetData?.categoryDesc)
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_desc)?.isEnabled = false
            // 아이콘 데이터 적용
            //아이콘 클릭 리스너 없음

            //스위치 데이터 적용
            //스위치 변경 불가
            view?.findViewById<Switch>(R.id.switch_category_public)?.isChecked =
                categoryEdiBottomSheetData!!.isPublic
            view?.findViewById<Switch>(R.id.switch_category_public)?.isEnabled = false

        }

        // 아이콘 Rv 연결
        val dCategoryIconAdapter = CategoryIconAdapter(sampleIconArray)
        dCategoryIconAdapter.setItemClickListener(object : CategoryIconAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                //선택 아이콘 변경
                //iv src 변경 적용
                //rv visibility 변경
                view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.GONE
            }

        })
        view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)?.adapter = dCategoryIconAdapter
        view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)?.layoutManager = GridLayoutManager(this.activity, 8)

    }
}