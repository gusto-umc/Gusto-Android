package com.gst.gusto.ListView.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
                binding.tvItemStoreEditLocation.text = item.address
                //리뷰 사진 3개
                if(!item.img1.isNullOrBlank()){
                    util.setImage(binding.ivItemStoreEditImg1, item.img1, mContext!!)
                }else{binding.ivItemStoreEditImg1.visibility = View.INVISIBLE}
                if(!item.img2.isNullOrBlank()){util.setImage(binding.ivItemStoreEditImg2, item.img2, mContext!!)
                }else{binding.ivItemStoreEditImg2.visibility = View.INVISIBLE}
                if(!item.img3.isNullOrBlank()){util.setImage(binding.ivItemStoreEditImg3, item.img3, mContext!!)
                }else{binding.ivItemStoreEditImg3.visibility = View.INVISIBLE}

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
                gustoViewModel!!.updateSelectFlag("change")
                holder.cb.isChecked = false

            }
            else{
                gustoViewModel!!.updateSelectFlag("change")
                holder.cb.isChecked = true
            }
        }

        holder.cb.setOnClickListener {
            if(holder.cb.isChecked){
                gustoViewModel!!.updateSelectFlag("change")
                holder.cb.isChecked = true

            }
            else{
                gustoViewModel!!.updateSelectFlag("change")
                holder.cb.isChecked = false
            }
        }

        holder.cb.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("select adapter check", gustoViewModel!!.allFlag.value.toString())
            if((gustoViewModel!!.allFlag.value == "all" && isChecked) || (gustoViewModel!!.allFlag.value == "nothing" && !isChecked)){
                Log.d("select adapter", "all or nothing")
            }
            else{
                if(isChecked){
                    gustoViewModel!!.selectedStoreIdList.add(holder.data!!.pinId)
                    holder.cb.isChecked = isChecked
                    Log.d("adapter select list on", gustoViewModel!!.selectedStoreIdList.toString())
                    if(gustoViewModel!!.selectedStoreIdList.size == gustoViewModel!!.myAllStoreList.size){
                        gustoViewModel!!.updateSelectFlag("changeOn")
                    }
                }
                else{
                    gustoViewModel!!.selectedStoreIdList.remove(holder.data!!.pinId)
                    holder.cb.isChecked = isChecked
                    Log.d("adapter select list off", gustoViewModel!!.selectedStoreIdList.toString())
                    if(gustoViewModel!!.selectedStoreIdList.size != gustoViewModel!!.myAllStoreList.size){
                        gustoViewModel!!.updateSelectFlag("changeOff")
                    }
                }
            }

        }
    }


}