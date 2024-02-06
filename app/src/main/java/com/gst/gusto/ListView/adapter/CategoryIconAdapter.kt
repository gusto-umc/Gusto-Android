package com.gst.gusto.ListView.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.databinding.ItemCategoryIconBinding

class CategoryIconAdapter(private val dataList : ArrayList<Int>) : RecyclerView.Adapter<CategoryIconAdapter.ViewHolder>(){

    var selectedIcon : String? = null

    inner class ViewHolder(private val binding : ItemCategoryIconBinding) : RecyclerView.ViewHolder(binding.root) {

        val categoryIcon : ImageView

        init {
            categoryIcon = binding.ivItemCategoryImg
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemCategoryIconBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryIcon.setImageResource(dataList[position].toInt())
        holder.categoryIcon.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
}