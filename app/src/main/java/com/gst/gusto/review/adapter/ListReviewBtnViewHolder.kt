package com.gst.gusto.review.adapter

import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewListButtonBinding

class ListReviewBtnViewHolder(binding: ItemReviewListButtonBinding) : RecyclerView.ViewHolder(binding.root) {
    val dateTextView = binding.date

    fun bind(date: String){
        dateTextView.text = date
    }
}