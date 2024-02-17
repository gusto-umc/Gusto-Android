package com.gst.gusto.ListView.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ListViewEditCategoryAdapter (private var flag : String, private val parentView : View) : ListAdapter<ResponseMapCategory, ListViewEditCategoryAdapter.ViewHolder>(
    DiffCallback) {


    var viewModel : GustoViewModel? = null

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemListviewCategoryEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var openFlag = false
        val cateId = holder.bind(getItem(position))

        holder.updownLayout.setOnClickListener {
            if(holder.data?.pinCnt != 0){
                if(openFlag){
                    holder.storeRv.visibility = View.GONE
                    holder.ivUpDown.setImageResource(R.drawable.arrow_down_2_img)
                    openFlag = false
                }
                else{
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
            Log.d("0개", "체")
            holder.ivUpDown.imageTintList = ColorStateList.valueOf(Color.parseColor("#ECECEC"))
        }




    }

}