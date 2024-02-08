package com.gst.gusto.review.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewCalendarBinding

class CalendarReviewAdapter(var imageList: Array<Int>, val context: Context?, private val itemClickListener: (Array<Int>) -> Unit) : RecyclerView.Adapter<CalendarReviewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CalendarReviewAdapter.ViewHolder {
        val binding: ItemReviewCalendarBinding = ItemReviewCalendarBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: CalendarReviewAdapter.ViewHolder, position: Int) {
        // val width = (context?.resources?.displayMetrics?.widthPixels ?: 0) / 3
        // holder.imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
        if(imageList[position] == 0){
            holder.imageview.setBackgroundColor(Color.parseColor("#ECECEC"))
        } else {
            holder.imageview.setImageResource(imageList[position])
        }

    }

    override fun getItemCount(): Int = imageList.size


    inner class ViewHolder(val binding: ItemReviewCalendarBinding, private val itemClickListener: (Array<Int>) -> Unit) : RecyclerView.ViewHolder(binding.root){
        val imageview : ImageView = binding.reviewImageView

        init {
            imageview.setOnClickListener{
                itemClickListener.invoke(imageList)
            }
        }

    }
}