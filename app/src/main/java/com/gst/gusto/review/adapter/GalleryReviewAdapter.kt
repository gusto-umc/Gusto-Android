package com.gst.gusto.review.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemReviewGalleryBinding

class GalleryReviewAdapter(var imageList: Array<Int>, val context: Context?, private val itemClickListener: (Array<Int>) -> Unit) : RecyclerView.Adapter<GalleryReviewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): GalleryReviewAdapter.ViewHolder {
        val binding: ItemReviewGalleryBinding = ItemReviewGalleryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: GalleryReviewAdapter.ViewHolder, position: Int) {
        val width = (context?.resources?.displayMetrics?.widthPixels ?: 0) / 3
        holder.imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
        holder.imageview.setImageResource(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size


    inner class ViewHolder(val binding: ItemReviewGalleryBinding, private val itemClickListener: (Array<Int>) -> Unit) : RecyclerView.ViewHolder(binding.root){
        val imageview : ImageView = binding.imageView

        init {
            imageview.setOnClickListener{
                itemClickListener.invoke(imageList)
            }
        }

    }
}