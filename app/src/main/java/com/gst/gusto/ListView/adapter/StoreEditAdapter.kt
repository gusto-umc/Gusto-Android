package com.gst.gusto.ListView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.PResponseStoreListItem
import com.gst.gusto.api.ResponseStoreListItem
import com.gst.gusto.databinding.ItemStoreBinding
import com.gst.gusto.databinding.ItemStoreEditBinding
import com.gst.gusto.util.util

class StoreEditAdapter : ListAdapter<PResponseStoreListItem, StoreEditAdapter.ViewHolder>(diffUtil){

    var gustoViewModel : GustoViewModel? = null
    var mContext : Context? = null

    inner class ViewHolder(private val binding : ItemStoreEditBinding) : RecyclerView.ViewHolder(binding.root){
        var data : PResponseStoreListItem? = null
        fun bind(item : PResponseStoreListItem){
            binding.apply {
                binding.tvItemStoreEditTitle.text = item.storeName
                //카테고리 -> 서버 추가 필요
                //binding.tvItemStoreCategory.text =
                binding.tvItemStoreEditLocation.text = item.address
                //리뷰 사진 3개
                if(!item.reviewImg3[0].isNullOrBlank()){
                    util.setImage(binding.ivItemStoreEditImg1, item.reviewImg3[0], mContext!!)
                }
                if(!item.reviewImg3[1].isNullOrBlank()){
                    util.setImage(binding.ivItemStoreEditImg2, item.reviewImg3[1], mContext!!)
                }
                if(!item.reviewImg3[2].isNullOrBlank()){
                    util.setImage(binding.ivItemStoreEditImg3, item.reviewImg3[2], mContext!!)
                }

            }
            data = item
        }
        val itemLayout : ConstraintLayout = binding.layoutItemStoreEdit
        val cb : CheckBox = binding.cbStoreEdit


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
        val viewHolder = ViewHolder(ItemStoreEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.cb.isChecked = holder.data!!.pinId in gustoViewModel!!.selectedStoreIdList

        holder.itemLayout.setOnClickListener {
            if(holder.cb.isChecked){
                gustoViewModel!!.selectedStoreIdList.remove(holder.data!!.pinId)
                holder.cb.isChecked = false
                if(gustoViewModel!!.selectedStoreIdList.size != gustoViewModel!!.myAllStoreList.size){
                    gustoViewModel!!.updateSelectFlag("false")
                }
            }
            else{
                gustoViewModel!!.selectedStoreIdList.add(holder.data!!.pinId)
                holder.cb.isChecked = true
                if(gustoViewModel!!.selectedStoreIdList.size == gustoViewModel!!.myAllStoreList.size){
                    gustoViewModel!!.updateSelectFlag("all")
                }
            }
        }

        holder.cb.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                gustoViewModel!!.selectedStoreIdList.add(holder.data!!.pinId)
                holder.cb.isChecked = isChecked
                if(gustoViewModel!!.selectedStoreIdList.size == gustoViewModel!!.myAllStoreList.size){
                    gustoViewModel!!.updateSelectFlag("all")
                }
            }
            else{
                gustoViewModel!!.selectedStoreIdList.remove(holder.data!!.pinId)
                holder.cb.isChecked = isChecked
                if(gustoViewModel!!.selectedStoreIdList.size != gustoViewModel!!.myAllStoreList.size){
                    gustoViewModel!!.updateSelectFlag("false")
                }
            }
        }
    }


}