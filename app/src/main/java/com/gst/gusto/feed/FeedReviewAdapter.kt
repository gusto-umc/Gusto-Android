package com.gst.gusto.feed


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.ResponseFeedReview
import com.gst.gusto.databinding.ItemReviewGalleryBinding

class FeedReviewAdapter(var feedList: ArrayList<ResponseFeedReview>, val context: Context?, private val itemClickListener: (Long) -> Unit) : RecyclerView.Adapter<FeedReviewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FeedReviewAdapter.ViewHolder {
        val binding: ItemReviewGalleryBinding = ItemReviewGalleryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: FeedReviewAdapter.ViewHolder, position: Int) {
        val width = (context?.resources?.displayMetrics?.widthPixels ?: 0) / 3
        holder.imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
        setImage(holder.imageview, feedList[position].images, holder.itemView.context)
    }

    override fun getItemCount(): Int = feedList.size


    inner class ViewHolder(val binding: ItemReviewGalleryBinding, private val itemClickListener: (Long) -> Unit) : RecyclerView.ViewHolder(binding.root){
        val imageview : ImageView = binding.imageView

        init {
            imageview.setOnClickListener{
                itemClickListener.invoke(feedList[position].reviewId)
            }
        }

    }
}