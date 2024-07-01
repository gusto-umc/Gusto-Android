package com.gst.gusto.review.adapter.viewholder

import android.content.Context
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewGalleryBinding
import com.gst.gusto.model.InstaReview
import com.gst.gusto.util.util.Companion.setImage

class InstaViewHolder(
    private val binding: ItemReviewGalleryBinding,
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            itemClickListener.invoke(adapterPosition)
        }
    }

    fun bind(item: InstaReview?, context: Context?){
        val width = (context?.resources?.displayMetrics?.widthPixels ?: 0) / 3
        binding.imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
        setImage(binding.imageView, item?.image, binding.root.context)
    }
}