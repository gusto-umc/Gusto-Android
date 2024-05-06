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
import com.gst.gusto.api.ResponseMapCategory
import com.gst.gusto.api.ResponseStoreListItem
import com.gst.gusto.databinding.CardWxampleBinding
import com.gst.gusto.databinding.ItemCategoryBinding
import com.gst.gusto.databinding.ItemStoreBinding

class StoreAdapter(private val parentView : View) : ListAdapter<ResponseStoreListItem, StoreAdapter.ViewHolder>(diffUtil){

    var gustoViewModel : GustoViewModel? = null
    var mContext : Context? = null

    inner class ViewHolder(private val binding : ItemStoreBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseStoreListItem? = null
        fun bind(item : ResponseStoreListItem){
            binding.apply {
                binding.tvItemStoreTitle.text = item.storeName
                //카테고리 -> 서버 추가 필요
                //binding.tvItemStoreCategory.text =
                binding.tvItemStoreLocation.text = item.address
                //리뷰 사진 3개 -> 서버 추가 필요
                binding.ivItemStoreImg1.setImageResource(R.drawable.gst_dummypic)
                binding.ivItemStoreImg2.setImageResource(R.drawable.gst_dummypic)
                binding.ivItemStoreImg3.setImageResource(R.drawable.gst_dummypic)
            }
            data = item
        }
        val itemLayout : ConstraintLayout = binding.layoutItemStore


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
        val viewHolder = ViewHolder(ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemLayout.setOnClickListener {
            //뷰모델에 storeId 저장 -> detail 로 이동
            gustoViewModel!!.selectedDetailStoreId = holder.data?.storeId!!.toInt()
            Navigation.findNavController(parentView).navigate(R.id.action_storeFragment_to_storeDetailFragment)

        }
    }


}