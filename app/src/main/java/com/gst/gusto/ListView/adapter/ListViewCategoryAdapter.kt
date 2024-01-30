package com.gst.gusto.ListView.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.R
import com.gst.gusto.databinding.ItemListviewCategoryShowBinding

class ListViewCategoryAdapter : ListAdapter<CategorySimple, ListViewCategoryAdapter.ViewHolder>(
    DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CategorySimple>(){
            override fun areItemsTheSame(oldItem: CategorySimple, newItem: CategorySimple): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CategorySimple, newItem: CategorySimple): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }


    inner class ViewHolder(private val binding : ItemListviewCategoryShowBinding) : RecyclerView.ViewHolder(binding.root){
        var data : CategorySimple? = null

        fun bind(simple: CategorySimple){
            data = simple
            binding.ivItemCategoryShow.setImageResource(R.drawable.category_icon_1)
            binding.tvItemCategoryShowTitle.text = simple.categoryName
            binding.tvCategoryShowCount.text = "${simple.storeCount}개"
        }
        val updownLayout = binding.layoutItemCategoryShowUpdown
        val storeRv = binding.rvItemCategoryShowStore
        val ivUpDown = binding.btnItemCategoryShowDown
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemListviewCategoryShowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var openFlag = false
        val cateId = holder.bind(getItem(position))

        holder.updownLayout.setOnClickListener {
            if(holder.data?.storeCount != 0){
                if(openFlag){
                    holder.storeRv.visibility = View.GONE
                    holder.ivUpDown.setImageResource(R.drawable.arrow_down_2_img)
                    openFlag = false
                }
                else{
                    holder.storeRv.visibility = View.VISIBLE
                    holder.ivUpDown.setImageResource(R.drawable.arrow_up_1_img)
                    openFlag = true
                }
            }
            else {

            }
        }

        if(holder.data?.storeCount!! <= 0) {
            holder.ivUpDown.imageTintList = ColorStateList.valueOf(Color.parseColor("#ECECEC"))
        }


    }

}