package com.gst.gusto.ListView.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.ListView.Model.StoreDetailReview
import com.gst.gusto.ListView.Model.StoreSearch
import com.gst.gusto.databinding.CardWxampleBinding
import com.gst.gusto.databinding.ItemStoreCardBinding

class SearchStoreAdapter() : ListAdapter<StoreSearch, SearchStoreAdapter.ViewHolder>(DiffCallback){

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<StoreSearch>(){
            override fun areItemsTheSame(oldItem: StoreSearch, newItem: StoreSearch): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.storeId == newItem.storeId
            }

            override fun areContentsTheSame(oldItem: StoreSearch, newItem: StoreSearch): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val binding : CardWxampleBinding) : RecyclerView.ViewHolder(binding.root){
        var data : StoreSearch? = null

        fun bind(result : StoreSearch){
            data = result
            //데이터 적용(가게명, 카테고리, 위치, 사진)
            binding.tvItemStoreTitle.text = result.storeName
            binding.tvItemStoreCount.text = result.categoryName
            //binding.tvItemStoreLocation.text =
            binding.ivItemStoreImg.setImageResource(result.reviewImg)
        }
        val cvItem = binding.cvItemStoreEdit
        val ivPhoto = binding.ivItemStoreImg
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(CardWxampleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))



        holder.cvItem.setOnClickListener {
            itemClickListener.onClick(it, holder.data!!)
        }

    }
    interface OnItemClickListener {
        fun onClick(v: View, dataSet: StoreSearch)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
}