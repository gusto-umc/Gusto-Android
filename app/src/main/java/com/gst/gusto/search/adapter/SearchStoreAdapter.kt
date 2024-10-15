package com.gst.gusto.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.api.ResponseSearch
import com.gst.gusto.api.ResponseSearch3
import com.gst.gusto.databinding.ItemStoreSearchBinding

class SearchStoreAdapter() : ListAdapter<ResponseSearch3, SearchStoreAdapter.ViewHolder>(DiffCallback){

    var mContext : Context? = null
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ResponseSearch3>(){
            override fun areItemsTheSame(oldItem: ResponseSearch3, newItem: ResponseSearch3): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.storeId == newItem.storeId
            }

            override fun areContentsTheSame(oldItem: ResponseSearch3, newItem: ResponseSearch3): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val binding : ItemStoreSearchBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseSearch3? = null

        fun bind(result : ResponseSearch3){
            data = result
            //데이터 적용(가게명, 카테고리, 위치, 사진)
            binding.tvItemStoreSearchTitle.text = result.storeName
            if(result.categoryString != null){
                binding.tvItemStoreSearchCategory.visibility = View.VISIBLE
                binding.tvItemStoreSearchCategory.text = result.categoryString
            }
            else{
                binding.tvItemStoreSearchCategory.visibility = View.INVISIBLE
            }
            binding.tvItemStoreSearchAddress.text = result.address
            setImage(binding.ivItemStoreSearchImg, result.reviewImg, mContext!!)
        }
        val cvItem = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemStoreSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))



        holder.cvItem.setOnClickListener {
            itemClickListener.onClick(it, holder.data!!)
        }

    }
    interface OnItemClickListener {
        fun onClick(v: View, dataSet: ResponseSearch3)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
}