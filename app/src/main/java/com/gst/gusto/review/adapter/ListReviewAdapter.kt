package com.gst.gusto.review.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewListBinding

class ListReviewAdapter(var dateList: Array<String>, var nameList: Array<String>, var visitList: Array<String>, var imageList: Array<Int>) : RecyclerView.Adapter<ListReviewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListReviewAdapter.ViewHolder {
        val binding: ItemReviewListBinding = ItemReviewListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListReviewAdapter.ViewHolder, position: Int) {
        holder.datetextview.text = dateList[position]
        holder.nametextview.text = nameList[position]
        holder.visittextview.text = visitList[position]
        holder.imageview1.setImageResource(imageList[position])
        holder.imageview2.setImageResource(imageList[position])
        holder.imageview3.setImageResource(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size


    inner class ViewHolder(val binding: ItemReviewListBinding) : RecyclerView.ViewHolder(binding.root){
        val datetextview = binding.dateTextView
        val nametextview = binding.nameTextView
        val visittextview = binding.visitTextView
        val imageview1 = binding.imageView1
        val imageview2 = binding.imageView2
        val imageview3 = binding.imageView3
    }
}