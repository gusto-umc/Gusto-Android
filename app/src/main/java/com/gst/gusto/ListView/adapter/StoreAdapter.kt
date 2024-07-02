package com.gst.gusto.ListView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.PResponseStoreListItem
import com.gst.gusto.api.ResponseMapCategory
import com.gst.gusto.api.ResponseSearch
import com.gst.gusto.api.ResponseStoreListItem
import com.gst.gusto.databinding.CardWxampleBinding
import com.gst.gusto.databinding.ItemCategoryBinding
import com.gst.gusto.databinding.ItemStoreBinding
import com.gst.gusto.util.util

class StoreAdapter(private val parentView : View, private var sign : String) : ListAdapter<PResponseStoreListItem, StoreAdapter.ViewHolder>(diffUtil){

    var gustoViewModel : GustoViewModel? = null
    var mContext : Context? = null

    inner class ViewHolder(private val binding : ItemStoreBinding) : RecyclerView.ViewHolder(binding.root){
        var data : PResponseStoreListItem? = null
        fun bind(item : PResponseStoreListItem){
            binding.apply {
                binding.tvItemStoreTitle.text = item.storeName
                //카테고리
                binding.tvItemStoreLocation.text = item.address
                //리뷰 사진 3개 -> 서버 추가 필요
                if(!item.reviewImg3[0].isNullOrBlank()){
                    util.setImage(binding.ivItemStoreImg1, item.reviewImg3[0], mContext!!)
                }
                if(!item.reviewImg3[1].isNullOrBlank()){
                    util.setImage(binding.ivItemStoreImg1, item.reviewImg3[1], mContext!!)
                }
                if(!item.reviewImg3[2].isNullOrBlank()){
                    util.setImage(binding.ivItemStoreImg1, item.reviewImg3[2], mContext!!)
                }
            }
            data = item
        }
        val itemLayout : ConstraintLayout = binding.layoutItemStore


    }
    companion object {
        // diffUtil: currentList에 있는 각 아이템들을 비교하여 최신 상태를 유지하도록 한다.
        val diffUtil = object : DiffUtil.ItemCallback<PResponseStoreListItem>() {
            override fun areItemsTheSame(oldItem: PResponseStoreListItem, newItem: PResponseStoreListItem): Boolean {
                return oldItem.storeId == newItem.storeId
            }

            override fun areContentsTheSame(oldItem: PResponseStoreListItem, newItem: PResponseStoreListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemLayout.setOnClickListener {
            itemClickListener.onClick(it, holder.data!!, sign)
        }
    }
    interface OnItemClickListener {
        fun onClick(v: View, dataSet: PResponseStoreListItem, sign : String)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener


}