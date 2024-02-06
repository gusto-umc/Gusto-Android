package com.gst.gusto.ListView.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.Model.StoreSearch
import com.gst.gusto.databinding.ItemMapCategoryChooseBinding

class CategoryChooseAdapter() : ListAdapter<CategorySimple, CategoryChooseAdapter.ViewHolder>(
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

    inner class ViewHolder(private val binding : ItemMapCategoryChooseBinding) : RecyclerView.ViewHolder(binding.root){
        var data : CategorySimple? = null

        fun bind(category : CategorySimple){
            data = category

            binding.ivItemCategoryChooseIcon.setImageResource(category.categoryIcon)
            binding.tvItemCategoryChooseTitle.text = category.categoryName
            binding.tvItemCategoryChooseCount.text = "${category.storeCount}개"
        }
        val layoutItem = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemMapCategoryChooseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.layoutItem.setOnClickListener {
            itemClickListener.onClick(it, holder.data!!)
        }

    }

    interface OnItemClickListener {
        fun onClick(v: View, dataSet: CategorySimple)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
}