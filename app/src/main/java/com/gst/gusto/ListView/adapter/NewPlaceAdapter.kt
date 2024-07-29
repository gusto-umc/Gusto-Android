package com.gst.gusto.ListView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.api.ResponseSavedStoreData
import com.gst.gusto.databinding.ItemStoreBinding

class NewPlaceAdapter(
    private val itemClickListener: (ResponseSavedStoreData) -> Unit
) : ListAdapter<ResponseSavedStoreData, NewPlaceAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemStoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseSavedStoreData) {
            binding.tvItemStoreTitle.text = item.storeName
            binding.tvItemStoreCategory.text = item.categoryName
            binding.tvItemStoreLocation.text = item.address

            /*
            // 이미지 로드 (Glide 사용)
            Glide.with(binding.root.context)
                .load(item.imageUrl1 ?: R.drawable.gst_dummypic)
                .centerCrop()
                .into(binding.ivItemStoreImg1)

            Glide.with(binding.root.context)
                .load(item.imageUrl2 ?: R.drawable.gst_dummypic)
                .centerCrop()
                .into(binding.ivItemStoreImg2)

            Glide.with(binding.root.context)
                .load(item.imageUrl3 ?: R.drawable.gst_dummypic)
                .centerCrop()
                .into(binding.ivItemStoreImg3)

            binding.root.setOnClickListener {
                itemClickListener(item)
            }
             */
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ResponseSavedStoreData>() {
            override fun areItemsTheSame(oldItem: ResponseSavedStoreData, newItem: ResponseSavedStoreData): Boolean {
                return oldItem.storeId == newItem.storeId
            }

            override fun areContentsTheSame(oldItem: ResponseSavedStoreData, newItem: ResponseSavedStoreData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
