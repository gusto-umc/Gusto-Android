package com.gst.gusto.review.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewListBinding
import com.gst.gusto.databinding.ItemReviewListButtonBinding
import com.gst.gusto.model.TimeLineReview
import com.gst.gusto.review.adapter.viewholder.ListReviewBtnViewHolder
import com.gst.gusto.review.adapter.viewholder.ListReviewViewHolder

class ListReviewAdapter(private val itemClickListener: (TimeLineReview) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: MutableList<TimeLineReview> = mutableListOf()
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == ListReviewType.LISTREVIEW){
            return ListReviewViewHolder(
                ItemReviewListBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup, false
                ), itemClickListener
            )
        }
        return ListReviewBtnViewHolder(
            ItemReviewListButtonBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            ), itemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(items[position].viewType){
            ListReviewType.LISTREVIEW -> {
                (holder as ListReviewViewHolder).bind(items[position])
            }
            ListReviewType.LISTBUTTON -> {
                (holder as ListReviewBtnViewHolder).bind(items[position])
            }

        }

    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<TimeLineReview>){
        items = list.toMutableList()
        notifyDataSetChanged()
    }
}