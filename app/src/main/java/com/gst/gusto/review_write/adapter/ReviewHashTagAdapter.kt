package com.gst.gusto.review_write.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel

class ReviewHashTagAdapter(private val intList: List<Int>) : RecyclerView.Adapter<ReviewHashTagAdapter.ReviewHashTagViewHolder>() {

    var gustoViewModel : GustoViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHashTagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_hashtags, parent, false)
        val viewHolder = ReviewHashTagViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ReviewHashTagViewHolder, position: Int) {
        val imageResId = intList[position]
        holder.tvHashTag.text = gustoViewModel!!.hashTag[position]
    }

    override fun getItemCount(): Int {
        return intList.size
    }

    inner class ReviewHashTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHashTag: TextView = itemView.findViewById(R.id.tv_item_review_hashtags)
    }
}