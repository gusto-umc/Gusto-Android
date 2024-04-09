package com.gst.gusto.ListView.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseMapCategory
import com.gst.gusto.databinding.ItemCategoryBinding



class CategoryAdapter(private val view : View) : ListAdapter<ResponseMapCategory, CategoryAdapter.ViewHolder>(diffUtil){
    var viewModel : GustoViewModel? = null
    inner class ViewHolder(private val binding : ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : ResponseMapCategory){
            binding.apply {
                tvItemCategoryTitle.text = item.categoryName
                ivItemCategoryIcon.setImageResource(viewModel!!.findIconResource(item!!.categoryIcon))
                tvItemCategoryCount.text = "${item.pinCnt}개"
            }
        }
        val categoryLayout = binding.layoutItemCategory
        val popup = binding.btnItemCategoryPopup
    }

    companion object {
        // diffUtil: currentList에 있는 각 아이템들을 비교하여 최신 상태를 유지하도록 한다.
        val diffUtil = object : DiffUtil.ItemCallback<ResponseMapCategory>() {
            override fun areItemsTheSame(oldItem: ResponseMapCategory, newItem: ResponseMapCategory): Boolean {
                return oldItem.myCategoryId == newItem.myCategoryId
            }

            override fun areContentsTheSame(oldItem: ResponseMapCategory, newItem: ResponseMapCategory): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])

        holder.categoryLayout.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_categoryFragment_to_storeFragment)
        }

        holder.popup.setOnClickListener {

        }
    }

}
