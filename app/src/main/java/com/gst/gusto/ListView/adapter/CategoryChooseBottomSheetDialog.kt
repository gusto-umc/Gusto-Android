package com.gst.gusto.ListView.adapter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseAddPin
import com.gst.gusto.api.ResponseMapCategory

class CategoryChooseBottomSheetDialog(var flag : String?, val itemClick : (Int, ResponseAddPin?) -> Unit) : BottomSheetDialogFragment() {

    var viewModel : GustoViewModel? = null

    private var categoryArray : List<ResponseMapCategory>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.map_choose_category_bottomsheetdialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //X 클릭리스너
        view?.findViewById<ImageView>(R.id.iv_category_choose_x)?.setOnClickListener {
            itemClick(0, null)
            dialog?.dismiss()
        }

        viewModel!!.myAllCategoryList.clear()

        val dCategoryChooseAdapter = CategoryChooseAdapter()
        dCategoryChooseAdapter.setItemClickListener(object : CategoryChooseAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: ResponseMapCategory) {
                //서버 연결 (찜)
                //storeId 받아오기!
                val storeId = if(flag == null){viewModel!!.myStoreDetail!!.storeId} else{flag!!.toInt()}
                viewModel!!.addPin(dataSet.myCategoryId.toLong(), storeId.toLong()){
                        result, data ->
                    when(result){
                        0 -> {
                            //성공
                            itemClick(1, data)
                            dialog?.dismiss()
                        }
                        1 -> {
                            //실패
                            itemClick(0, data)
                            dialog?.dismiss()
                        }

                    }
                }
            }

        })
        dCategoryChooseAdapter.submitList(viewModel!!.myAllCategoryList)
        dCategoryChooseAdapter.viewModel = viewModel!!
        view?.findViewById<RecyclerView>(R.id.rv_category_choose)?.adapter = dCategoryChooseAdapter
        view?.findViewById<RecyclerView>(R.id.rv_category_choose)?.layoutManager = LinearLayoutManager(this.requireActivity())

        var hasNext = false

        viewModel!!.getPPMyCategory(null){
                result, getHasNext ->
            when(result){
                1 -> {
                    //success
                    dCategoryChooseAdapter.submitList(viewModel!!.myAllCategoryList)
                    hasNext = getHasNext
                    dCategoryChooseAdapter.notifyDataSetChanged()
                }
                else-> {
                    Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }

            }
        }

        view?.findViewById<RecyclerView>(R.id.rv_category_choose)?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                // 페이징 처리
                if(rvPosition == totalCount && hasNext) {
                    viewModel!!.getPPMyCategory(viewModel!!.myAllCategoryList.last().myCategoryId) {result, getHasNext ->
                        hasNext = getHasNext
                        when(result) {
                            1 -> {
                                val handler = Handler(Looper.getMainLooper())
                                handler.postDelayed({
                                    dCategoryChooseAdapter.submitList(viewModel!!.myAllCategoryList)
                                    dCategoryChooseAdapter.notifyDataSetChanged()
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