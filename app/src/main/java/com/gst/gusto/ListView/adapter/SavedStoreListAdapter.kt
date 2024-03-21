package com.gst.gusto.ListView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.api.ResponseSavedStoreData
import com.gst.gusto.databinding.ItemListviewStoreCardBinding

class SavedStoreListAdapter(private var flag : String, private val parentView : View) : ListAdapter<ResponseSavedStoreData, SavedStoreListAdapter.ViewHolder>(DiffCallback) {

    var mContext : Context? = null
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ResponseSavedStoreData>(){
            override fun areItemsTheSame(oldItem: ResponseSavedStoreData, newItem: ResponseSavedStoreData): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.storeId == newItem.storeId
            }

            override fun areContentsTheSame(oldItem: ResponseSavedStoreData, newItem: ResponseSavedStoreData): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val binding : ItemListviewStoreCardBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseSavedStoreData? = null
        fun bind(store: ResponseSavedStoreData){
            data = store
            setImage(binding.ivItemListviewCardImg, store.reviewImg, mContext!!)
            binding.tvItemListviewCardTitle.text = store.storeName
            binding.tvItemListviewCardLocation.text = store.address
            binding.tvItemListviewCardCount.text = store.categoryName
        }
        var layoutData = binding.cvItemListviewCard
    }



    interface OnItemClickListener {
        fun onClick(v: View, dataSet: ResponseSavedStoreData)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedStoreListAdapter.ViewHolder {
        val viewHolder = ViewHolder(ItemListviewStoreCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: SavedStoreListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.layoutData.setOnClickListener {
            itemClickListener.onClick(it, holder.data!!)
        }

    }
}