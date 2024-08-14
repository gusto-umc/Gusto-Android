package com.gst.gusto.ListView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gst.gusto.R
import com.gst.gusto.api.StoreData
import com.gst.gusto.databinding.ItemStoreBinding

class SavedStoreListAdapter :
    ListAdapter<StoreData, SavedStoreListAdapter.StoreViewHolder>(StoreDiffCallback()) {

    private var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = getItem(position)
        holder.bind(store)
    }

    inner class StoreViewHolder(private val binding: ItemStoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(store: StoreData) {
            binding.store = store
            binding.executePendingBindings()
            // 이미지 로드
            loadImage(binding.ivItemStoreImg1, store.reviewImg3.getOrNull(0))
            loadImage(binding.ivItemStoreImg2, store.reviewImg3.getOrNull(1))
            loadImage(binding.ivItemStoreImg3, store.reviewImg3.getOrNull(2))

            // 클릭 리스너 설정
            binding.root.setOnClickListener {
                itemClickListener?.onClick(store)
            }
        }

        private fun loadImage(imageView: ImageView, imageUrl: String?) {
            if (imageUrl != null) {
                Glide.with(imageView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.gst_dummypic) // 로딩 중 기본 이미지
                    .error(R.drawable.gst_dummypic) // 로드 실패 시 기본 이미지
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.gst_dummypic) // 기본 이미지
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(dataSet: StoreData)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
}

private class StoreDiffCallback : DiffUtil.ItemCallback<StoreData>() {
    override fun areItemsTheSame(oldItem: StoreData, newItem: StoreData): Boolean {
        return oldItem.storeId == newItem.storeId
    }

    override fun areContentsTheSame(oldItem: StoreData, newItem: StoreData): Boolean {
        return oldItem == newItem
    }
}
