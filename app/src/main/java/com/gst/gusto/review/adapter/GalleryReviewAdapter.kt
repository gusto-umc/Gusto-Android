package com.gst.gusto.review.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.ResponseInstaReview
import com.gst.gusto.api.ResponseInstaReviews
import com.gst.gusto.databinding.ItemReviewGalleryBinding

class GalleryReviewAdapter(var galleryList: ArrayList<ResponseInstaReviews>, val context: Context?, private val itemClickListener: (Long) -> Unit) : RecyclerView.Adapter<GalleryReviewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): GalleryReviewAdapter.ViewHolder {
        val binding: ItemReviewGalleryBinding = ItemReviewGalleryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: GalleryReviewAdapter.ViewHolder, position: Int) {
        val width = (context?.resources?.displayMetrics?.widthPixels ?: 0) / 3
        holder.imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
        setImage(holder.imageview, galleryList[position].images, holder.itemView.context)
    }

    override fun getItemCount(): Int = galleryList.size


    inner class ViewHolder(val binding: ItemReviewGalleryBinding, private val itemClickListener: (Long) -> Unit) : RecyclerView.ViewHolder(binding.root){
        val imageview : ImageView = binding.imageView

        init {
            imageview.setOnClickListener{
                itemClickListener.invoke(galleryList[position].reviewId)
            }
        }

    }
}