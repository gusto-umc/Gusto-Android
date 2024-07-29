package com.gst.gusto.review.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewListButtonBinding
import com.gst.gusto.model.TimeLineReview

class ListReviewBtnViewHolder(
    private val binding: ItemReviewListButtonBinding,
    private val itemClickListener: (TimeLineReview) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(listReviewList: TimeLineReview) {
        binding.addButton.setOnClickListener {
            itemClickListener.invoke(listReviewList)
        }
    }

}