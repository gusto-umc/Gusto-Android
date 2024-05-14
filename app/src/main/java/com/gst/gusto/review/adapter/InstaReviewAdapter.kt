package com.gst.gusto.review.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.databinding.ItemBufferingBinding
import com.gst.gusto.databinding.ItemReviewGalleryBinding
import com.gst.gusto.model.InstaReview
import com.gst.gusto.review.adapter.viewholder.InstaViewHolder
import com.gst.gusto.review.adapter.viewholder.LoadingViewHolder

class InstaReviewAdapter(
    private val context: Context?,
    private val itemClickListener: (Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: MutableList<InstaReview?> = mutableListOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_buffering -> {
                LoadingViewHolder(ItemBufferingBinding.inflate(LayoutInflater.from(viewGroup.context),
                    viewGroup, false)
                )
            }
            R.layout.item_review_gallery -> {
                val binding = ItemReviewGalleryBinding.inflate(
                    LayoutInflater.from(viewGroup.context), viewGroup, false)
                InstaViewHolder(binding) { position ->
                    items[position]?.reviewId?.let { itemClickListener(it) }
                }
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            R.layout.item_buffering -> {
                val loadingPosition = items.withIndex().filter { it.value == null }.map { it.index }
                (holder as LoadingViewHolder).bind(position, loadingPosition)
            }
            R.layout.item_review_gallery -> {
                (holder as InstaViewHolder).bind(items.get(position), context)
            }
        }
    }

    override fun getItemCount(): Int = items.size ?: 0

    override fun getItemViewType(position: Int): Int = when (items.get(position)) {
        null -> R.layout.item_buffering
        else -> R.layout.item_review_gallery
    }

    fun addItems(newItems: List<InstaReview?>) {
        val isLoadingRemoved = removeLoading()

        items = newItems.toMutableList()
        addLoading()

        if (isLoadingRemoved) {
            notifyDataSetChanged()
        }

    }

    fun addLoading(): Boolean {
        if (!items.contains(null)) {
            items.add(null)
            items.add(null)
            items.add(null)
            items.size.minus(1).let { notifyItemInserted(it) }
            return true
        }
        return false
    }

    fun removeLoading(count: Int = 3): Boolean {
        var removedCount = 0
        for (i in 1..count) {
            if (items.isNotEmpty() && items.last() == null) {
                val lastPosition = items.size.minus(1)
                items.removeAt(lastPosition)
                notifyItemRemoved(lastPosition)
                removedCount++
            }
        }
        return removedCount > 0
    }
}