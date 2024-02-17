package com.gst.gusto.ListView.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseMapCategory
import com.gst.gusto.databinding.ItemListviewCategoryEditBinding

class ListViewEditCategoryAdapter (private var flag : String, private val parentView : View, private val cbAll : CheckBox) : ListAdapter<ResponseMapCategory, ListViewEditCategoryAdapter.ViewHolder>(
    DiffCallback) {


    var viewModel : GustoViewModel? = null
    var selectedAllCategoryFlag : Boolean? = null


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ResponseMapCategory>(){
            override fun areItemsTheSame(oldItem: ResponseMapCategory, newItem: ResponseMapCategory): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.myCategoryId == newItem.myCategoryId
            }

            override fun areContentsTheSame(oldItem: ResponseMapCategory, newItem: ResponseMapCategory): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }


    inner class ViewHolder(private val binding : ItemListviewCategoryEditBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseMapCategory? = null

        fun bind(simple: ResponseMapCategory){
            data = simple
            binding.ivItemCategoryEdit.setImageResource(R.drawable.category_icon_1)
            binding.tvItemCategoryEditTitle.text = simple.categoryName
            binding.tvItemCategoryEditCount.text = "${simple.pinCnt}개"
        }
        val updownLayout = binding.layoutItemCategoryEditUpdown
        val storeRv = binding.rvItemCategoryShowStore
        val ivUpDown = binding.btnItemCategoryEditDown
        val cb = binding.cbItemCategoryEdit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemListviewCategoryEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var openFlag = false
        val cateId = holder.bind(getItem(position))


        //카테고리s 전체 선택 시
        if(selectedAllCategoryFlag == true){
            holder.cb.isChecked = true
        }
        else{
            holder.cb.isChecked = false
        }

        //cb처리
        holder.cb.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                //전체 선택 여부 확인
                Log.d("categoryId", holder.data!!.toString())
                viewModel!!.selectedCategory.add(holder.data!!.myCategoryId)
                if(viewModel!!.selectedCategory.size == viewModel!!.myMapCategoryList!!.size){
                    viewModel!!.changeCategoryFlag(true)
                }
            }
            else{
                viewModel!!.selectedCategory.remove(holder.data!!.myCategoryId)
                viewModel!!.cateRemoveFlag = true
                cbAll.isChecked = false
            }
        }


        holder.updownLayout.setOnClickListener {
            if(holder.data?.pinCnt != 0){
                if(openFlag){
                    holder.storeRv.visibility = View.GONE
                    holder.ivUpDown.setImageResource(R.drawable.arrow_down_2_img)
                    openFlag = false
                    //뷰모델에 신호 -> 해당 카테고리 외에 숨기기
                }
                else{
                    //뷰모델에 신호 -> 모든 카테고리 활성화
                    holder.storeRv.visibility = View.VISIBLE
                    holder.ivUpDown.setImageResource(R.drawable.arrow_up_1_img)
                    openFlag = true
                    /**
                     * storeRv 연결
                     */
                    viewModel!!.getMapStores(holder.data!!.myCategoryId, townName = "성수1가1동"){
                            result ->
                        when(result){
                            0 -> {
                                //성공
                                val mStoreAdapter = ListViewStoreAdapter(flag, parentView)
                                mStoreAdapter.submitList(viewModel!!.myMapStoreList!!)
                                holder.storeRv.adapter = mStoreAdapter
                                holder.storeRv.layoutManager = LinearLayoutManager(holder.storeRv.context, LinearLayoutManager.VERTICAL, false)
                            }
                            1 -> {
                                //실패
                                Log.d("store checking", "fail")
                            }
                        }
                    }
                }
            }
            else {

            }
        }

        if(holder.data?.pinCnt!! <= 0) {
            holder.ivUpDown.imageTintList = ColorStateList.valueOf(Color.parseColor("#ECECEC"))
        }




    }

}