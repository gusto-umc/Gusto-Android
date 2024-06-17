package com.gst.gusto.ListView.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.Model.StoreSearch
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseAllCategory
import com.gst.gusto.api.ResponseMapCategory
import com.gst.gusto.databinding.ItemMapCategoryChooseBinding

class CategoryChooseAdapter() : ListAdapter<ResponseMapCategory, CategoryChooseAdapter.ViewHolder>(
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

    inner class ViewHolder(private val binding : ItemMapCategoryChooseBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseMapCategory? = null

        fun bind(category : ResponseMapCategory){
            data = category

            binding.ivItemCategoryChooseIcon.setImageResource(viewModel!!.findIconResource(category.categoryIcon))
            binding.tvItemCategoryChooseTitle.text = category.categoryName
            binding.tvItemCategoryChooseCount.text = "${category.pinCnt}개"
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
        fun onClick(v: View, dataSet: ResponseMapCategory)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
}