package com.gst.gusto.review.adapter


import android.content.Context
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
    var items: MutableList<InstaReview?>?,
    private val context: Context?,
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_buffering -> {
                LoadingViewHolder(ItemBufferingBinding.inflate(LayoutInflater.from(viewGroup.context),
                    viewGroup, false)
                )
            }
            R.layout.item_review_gallery -> {
                InstaViewHolder(ItemReviewGalleryBinding.inflate(LayoutInflater.from(viewGroup.context),
                    viewGroup, false),
                    itemClickListener
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            R.layout.item_buffering -> {
                val loadingPosition = items?.withIndex()?.filter { it.value == null }?.map { it.index } ?: emptyList()
                (holder as LoadingViewHolder).bind(position, loadingPosition)
            }
            R.layout.item_review_gallery -> {
                (holder as InstaViewHolder).bind(items?.get(position), context)
            }
        }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    override fun getItemViewType(position: Int): Int = when (items?.get(position)) {
        null -> R.layout.item_buffering
        else -> R.layout.item_review_gallery
    }

    fun addItems(newItems: List<InstaReview>) {
        val isLoadingRemoved = removeLoading()

        val startPosition = items?.size ?: 0 // 시작 위치 갱신
        items?.addAll(newItems)

        if (isLoadingRemoved) {
            notifyItemRangeInserted(startPosition, newItems.size)
        } else {
            notifyItemRangeInserted(startPosition, newItems.size + 3)
        }

        addLoading()
    }

    fun addLoading(): Boolean {
        if (items?.contains(null) == false) {
            items?.add(null)
            items?.add(null)
            items?.add(null)
            items?.size?.minus(1)?.let { notifyItemInserted(it) }
            return true
        }
        return false
    }

    fun removeLoading(count: Int = 3): Boolean {
        var removedCount = 0
        for (i in 1..count) {
            if (items?.isNotEmpty() == true && items?.last() == null) {
                val lastPosition = items?.size?.minus(1)
                if (lastPosition != null) {
                    items?.removeAt(lastPosition)
                    notifyItemRemoved(lastPosition)
                    removedCount++
                }
            }
        }
        return removedCount > 0
    }
}