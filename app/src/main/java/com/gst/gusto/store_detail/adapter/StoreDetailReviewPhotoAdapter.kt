package com.gst.gusto.store_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.databinding.ItemStoreDetailReviewPhotoBinding


class StoreDetailReviewPhotoAdapter (val dataSet : ArrayList<Int>) : RecyclerView.Adapter<StoreDetailReviewPhotoAdapter.ViewHolder>(){

    inner class ViewHolder(private val binding: ItemStoreDetailReviewPhotoBinding) : RecyclerView.ViewHolder(binding.root){
        var data : Int? = null

        fun bind(photo : Int){
            binding.ivItemStoreDetailReviewPhoto.setImageResource(photo)
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