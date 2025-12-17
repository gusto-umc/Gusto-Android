package com.gst.gusto.ListView.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel

/**
 * 카테고리 선택 바텀시트 다이얼로그 (cateMove 용)
 */
class CategorySelectBottomSheetDialog(
    private val viewModel: GustoViewModel,
    private val parentView : View
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.category_select_bottomsheetdialog_move, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 닫기 버튼
        view.findViewById<ImageView>(R.id.iv_bottomsheet_category_x)?.setOnClickListener {
            dialog?.dismiss()
        }

        // RecyclerView 세팅
        val rv = view.findViewById<RecyclerView>(R.id.rv_bottomsheet_category_list)
        val adapter = CategoryAdapter(view, "cateMove", parentFragmentManager)
        adapter.viewModel = viewModel
        adapter.mContext = context

        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter

        // ViewModel에서 카테고리 목록 가져오기
        adapter.submitList(viewModel.myAllCategoryList)

        // 아이템 선택 이벤트
        adapter.setItemChangeListener(object : CategoryAdapter.OnItemChangeListener {
            override fun onChange(v: View, flag: String) {
                viewModel.moveStores(viewModel.selectedStoreIdList){
                        result ->
                    when(result){
                        0 -> {
                            //success
                            Navigation.findNavController(parentView).popBackStack()
                            dialog?.dismiss()
                        }
                        1 -> {
                            //fail
                            Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                }

            }
        })
    }
}
