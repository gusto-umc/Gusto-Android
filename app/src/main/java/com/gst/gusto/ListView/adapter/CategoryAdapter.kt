package com.gst.gusto.ListView.adapter

import android.text.Layout
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseMapCategory
import com.gst.gusto.databinding.ItemCategoryBinding



class CategoryAdapter(private val view : View, private var optionsMenuClickListener: OptionsMenuClickListener) : ListAdapter<ResponseMapCategory, CategoryAdapter.ViewHolder>(diffUtil){
    var viewModel : GustoViewModel? = null
    inner class ViewHolder(private val binding : ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseMapCategory? = null
        fun bind(item : ResponseMapCategory){
            binding.apply {
                tvItemCategoryTitle.text = item.categoryName
                ivItemCategoryIcon.setImageResource(viewModel!!.findIconResource(item!!.categoryIcon))
                tvItemCategoryCount.text = "${item.pinCnt}개"
            }
            data = item
        }
        val categoryLayout = binding.layoutItemCategory
        val popup = binding.btnItemCategoryPopup
        val cv = binding.cvCategoryMenu


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
    interface OptionsMenuClickListener {
        fun onOptionsMenuClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])

        holder.categoryLayout.setOnClickListener {
            viewModel!!.selectedCategoryInfo = holder.data
            Navigation.findNavController(view).navigate(R.id.action_categoryFragment_to_storeFragment)
        }

        holder.popup.setOnClickListener {
            optionsMenuClickListener.onOptionsMenuClicked(position)
        }
    }

}
