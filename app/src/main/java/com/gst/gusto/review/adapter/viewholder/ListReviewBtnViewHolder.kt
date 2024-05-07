package com.gst.gusto.review.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewListButtonBinding
import com.gst.gusto.review.adapter.ListReviewData

class ListReviewBtnViewHolder(private val binding: ItemReviewListButtonBinding, private val itemClickListener: (ListReviewData) -> Unit) : RecyclerView.ViewHolder(binding.root) {
    val dateTextView = binding.date
    val frameLayout = binding.frameLayout

    fun bind(listReviewList: ListReviewData){
        dateTextView.text = listReviewList.date
        frameLayout.setOnClickListener{
            itemClickListener.invoke(listReviewList)
        }
    }

}