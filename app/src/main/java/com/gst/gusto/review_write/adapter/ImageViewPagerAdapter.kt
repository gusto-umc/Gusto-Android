package com.gst.gusto.review_write.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.setImage

class ImageViewPagerAdapter(private val imageList: List<Int>) : RecyclerView.Adapter<ImageViewPagerAdapter.ReviewDetailViewHolder>() {
    val imageViewList = ArrayList<ImageView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_detail_image, parent, false)
        return ReviewDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewDetailViewHolder, position: Int) {
        val imageResId = imageList[position]
        holder.imageView.setImageResource(imageResId)
        imageViewList.add(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
    public fun setImageView(i : Int, uri : String,context : Context) {
        setImage(imageViewList[i], uri, context)
    }

    inner class ReviewDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_rest_img)
    }
}
