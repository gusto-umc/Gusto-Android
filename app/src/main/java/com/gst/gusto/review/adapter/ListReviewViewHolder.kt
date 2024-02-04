package com.gst.gusto.review.adapter

import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewListBinding

class ListReviewViewHolder(
    private val binding: ItemReviewListBinding,
    private val itemClickListener: (ListReviewData) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    val datetextview = binding.dateTextView
    val nametextview = binding.nameTextView
    val visittextview = binding.visitTextView
    val imageview1 = binding.imageView1
    val imageview2 = binding.imageView2
    val imageview3 = binding.imageView3
    val linearLayout = binding.linearLayout

    fun bind(listReviewList: ListReviewData){
        datetextview.text = listReviewList.date
        nametextview.text = listReviewList.name
        visittextview.text = listReviewList.visit
        imageview1.setImageResource(listReviewList.imageview1)
        imageview2.setImageResource(listReviewList.imageview2)
        imageview3.setImageResource(listReviewList.imageview3)
        linearLayout.setOnClickListener {
            itemClickListener.invoke(listReviewList)
        }
    }
}