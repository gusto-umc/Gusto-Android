package com.gst.gusto.review.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.ResponseCalReview
import com.gst.gusto.api.ResponseCalReviews
import com.gst.gusto.databinding.ItemReviewCalendarBinding

class CalendarReviewAdapter(var calendarList: List<ResponseCalReviews?>, val context: Context?, private val itemClickListener: (Long) -> Unit) : RecyclerView.Adapter<CalendarReviewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CalendarReviewAdapter.ViewHolder {
        val binding: ItemReviewCalendarBinding = ItemReviewCalendarBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setImage(holder.imageview,
            (calendarList[position]?.images?: null).toString(), holder.itemView.context)

    }

    override fun getItemCount(): Int = calendarList.size


    inner class ViewHolder(val binding: ItemReviewCalendarBinding, private val itemClickListener: (Long) -> Unit) : RecyclerView.ViewHolder(binding.root){
        val imageview : ImageView = binding.reviewImageView

        init {
            imageview.setOnClickListener{
                itemClickListener.invoke(calendarList[position]?.reviewId ?: 0L)
            }
        }

    }
}