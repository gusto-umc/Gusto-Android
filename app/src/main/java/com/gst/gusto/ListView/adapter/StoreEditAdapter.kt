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
import com.gst.gusto.api.ResponseStoreListItem
import com.gst.gusto.databinding.ItemStoreBinding
import com.gst.gusto.databinding.ItemStoreEditBinding

class StoreEditAdapter : ListAdapter<ResponseStoreListItem, StoreEditAdapter.ViewHolder>(diffUtil){

    var gustoViewModel : GustoViewModel? = null
    var mContext : Context? = null

    inner class ViewHolder(private val binding : ItemStoreEditBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseStoreListItem? = null
        fun bind(item : ResponseStoreListItem){
            binding.apply {
                binding.tvItemStoreEditTitle.text = item.storeName
                //카테고리 -> 서버 추가 필요
                //binding.tvItemStoreCategory.text =
                binding.tvItemStoreEditLocation.text = item.address
                //리뷰 사진 3개 -> 서버 추가 필요
                binding.ivItemStoreEditImg1.setImageResource(R.drawable.gst_dummypic)
                binding.ivItemStoreEditImg2.setImageResource(R.drawable.gst_dummypic)
                binding.ivItemStoreEditImg3.setImageResource(R.drawable.gst_dummypic)
            }
            data = item
        }
        val itemLayout : ConstraintLayout = binding.layoutItemStoreEdit
        val cb : CheckBox = binding.cbStoreEdit


    }
    companion object {
        // diffUtil: currentList에 있는 각 아이템들을 비교하여 최신 상태를 유지하도록 한다.
        val diffUtil = object : DiffUtil.ItemCallback<ResponseStoreListItem>() {
            override fun areItemsTheSame(oldItem: ResponseStoreListItem, newItem: ResponseStoreListItem): Boolean {
                return oldItem.storeId == newItem.storeId
            }

            override fun areContentsTheSame(oldItem: ResponseStoreListItem, newItem: ResponseStoreListItem): Boolean {
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

        holder.cb.isChecked = holder.data!!.storeId in gustoViewModel!!.selectedStoreIdList

        holder.itemLayout.setOnClickListener {
            if(holder.cb.isChecked){
                gustoViewModel!!.selectedStoreIdList.remove(holder.data!!.storeId)
                holder.cb.isChecked = false
            }
            else{
                gustoViewModel!!.selectedStoreIdList.add(holder.data!!.storeId)
                holder.cb.isChecked = true
            }
        }

        holder.cb.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                gustoViewModel!!.selectedStoreIdList.remove(holder.data!!.storeId)
                holder.cb.isChecked = false
            }
            else{
                gustoViewModel!!.selectedStoreIdList.add(holder.data!!.storeId)
                holder.cb.isChecked = true
            }
        }
    }


}