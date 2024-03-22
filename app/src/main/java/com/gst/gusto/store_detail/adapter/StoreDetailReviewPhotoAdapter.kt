package com.gst.gusto.store_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.databinding.ItemStoreDetailReviewPhotoBinding


class StoreDetailReviewPhotoAdapter (val dataSet : ArrayList<String?>) : RecyclerView.Adapter<StoreDetailReviewPhotoAdapter.ViewHolder>(){

    var mContext : Context? = null
    inner class ViewHolder(private val binding: ItemStoreDetailReviewPhotoBinding) : RecyclerView.ViewHolder(binding.root){
        var data : String? = null

        fun bind(photo : String?){
            //binding.ivItemStoreDetailReviewPhoto.setImageResource(photo)
            if(!photo.isNullOrBlank()){
                setImage(binding.ivItemStoreDetailReviewPhoto, photo, mContext!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoreDetailReviewPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }


}