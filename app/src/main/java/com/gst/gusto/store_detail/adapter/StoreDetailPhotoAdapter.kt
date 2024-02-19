package com.gst.gusto.store_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.databinding.ItemStoreDetailPhotoBinding

class StoreDetailPhotoAdapter(val dataSet : ArrayList<String>) : RecyclerView.Adapter<StoreDetailPhotoAdapter.ViewHolder>(){

    var mContext : Context? = null
    inner class ViewHolder(private val binding: ItemStoreDetailPhotoBinding) : RecyclerView.ViewHolder(binding.root){
        var data : String? = null

        fun bind(photo : String){
            data = photo
            if(mContext != null){
                setImage(binding.ivItemStoreDetailPhoto, photo, mContext!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoreDetailPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }


}