package com.gst.gusto.review.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemBufferingBinding

class LoadingViewHolder(
    private val binding: ItemBufferingBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int, loadingPosition: List<Int>) {
        if (loadingPosition.size == 3 && position == loadingPosition[1]) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}