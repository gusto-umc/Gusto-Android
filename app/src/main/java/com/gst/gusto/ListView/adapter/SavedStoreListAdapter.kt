package com.gst.gusto.ListView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.api.ResponseSavedStoreData
import com.gst.gusto.databinding.ItemListviewStoreCardBinding

class SavedStoreListAdapter(private val flag: String, private val parentView: View) : ListAdapter<ResponseSavedStoreData, SavedStoreListAdapter.ViewHolder>(DiffCallback) {

    var mContext: Context? = null

    private lateinit var itemClickListener: OnItemClickListener


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ResponseSavedStoreData>() {
            override fun areItemsTheSame(oldItem: ResponseSavedStoreData, newItem: ResponseSavedStoreData): Boolean {
                return oldItem.storeId == newItem.storeId
            }

            override fun areContentsTheSame(oldItem: ResponseSavedStoreData, newItem: ResponseSavedStoreData): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(private val binding: ItemListviewStoreCardBinding) : RecyclerView.ViewHolder(binding.root) {
        private var data: ResponseSavedStoreData? = null

        init {
            binding.root.setOnClickListener {
                data?.let { itemClickListener.onClick(it) }
            }
        }

        fun bind(store: ResponseSavedStoreData) {
            data = store
            // 이미지 설정 예시 (setImage 함수를 구현하여 사용할 것)
            // setImage(binding.ivItemListviewCardImg, store.reviewImg3, mContext!!)
            binding.tvItemListviewCardTitle.text = store.storeName
            binding.tvItemListviewCardLocation.text = store.address
            binding.tvItemListviewCardCount.text = store.categoryName

            // 플래그에 따른 동작 처리
            when (flag) {
                "save" -> {
                    // '저장' 플래그에 따른 동작 처리
                }
                "visited" -> {
                    // '방문' 플래그에 따른 동작 처리
                }
                // 필요에 따라 추가적인 플래그 처리 가능
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(dataSet: ResponseSavedStoreData)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListviewStoreCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
