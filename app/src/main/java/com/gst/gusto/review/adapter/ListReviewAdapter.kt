package com.gst.gusto.review.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewListBinding
import com.gst.gusto.databinding.ItemReviewListButtonBinding

class ListReviewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = ArrayList<ListReviewData>()
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == ListReviewType.LISTREVIEW){
            return ListReviewViewHolder(
                ItemReviewListBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup, false
                )
            )
        }
        return ListReviewBtnViewHolder(
            ItemReviewListButtonBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(items[position].viewType){
            ListReviewType.LISTREVIEW -> {
                (holder as ListReviewViewHolder).bind(items[position])
            }
            ListReviewType.LISTBUTTON -> {
                (holder as ListReviewBtnViewHolder).bind(items[position].date)
            }

        }

    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<ListReviewData>){
        items = list
        notifyDataSetChanged()
    }
}