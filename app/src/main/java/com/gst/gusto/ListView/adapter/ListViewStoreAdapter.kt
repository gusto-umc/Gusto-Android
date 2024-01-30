package com.gst.gusto.ListView.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.R
import com.gst.gusto.databinding.CardWxampleBinding
import com.gst.gusto.databinding.ItemStoreCardBinding

class ListViewStoreAdapter(private var flag : String) : ListAdapter<Store, ListViewStoreAdapter.ViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Store>(){
            override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val binding : CardWxampleBinding) : RecyclerView.ViewHolder(binding.root){
        var data : Store? = null
        fun bind(store: Store){
            data = store
            binding.ivItemStoreImg.setImageResource(R.drawable.sample_store_img)
            binding.tvItemStoreTitle.text = store.storeName
            binding.tvItemStoreLocation.text = store.location
        }
        val cbEdit = binding.cbItemStoreEdit
        val tvCountCategory = binding.tvItemStoreCount
        val cvStore = binding.cvItemStoreEdit
        val tvStoreName = binding.tvItemStoreTitle
        val tvStoreLocation = binding.tvItemStoreLocation
        var ivPhoto = binding.ivItemStoreImg
        var layoutData = binding.layoutCardData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(CardWxampleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        if(flag == "show"){
            // 글자크기 조정
            holder.tvStoreLocation.setTextSize(8F)
            holder.tvStoreName.setTextSize(13F)
            holder.tvCountCategory.setTextSize(8F)

            holder.cbEdit.visibility = View.GONE
            holder.tvCountCategory.text = "${holder.data?.visitCount}번 방문했어요"
            holder.cvStore.setOnClickListener {
                Log.d("store show", "store name : ${holder.data?.storeName}")
            }
        }
        else if(flag == "edit"){
            // 글자크기 조정
            holder.tvStoreLocation.setTextSize(8F)
            holder.tvStoreName.setTextSize(13F)
            holder.tvCountCategory.setTextSize(8F)

            holder.cbEdit.visibility = View.VISIBLE
            holder.cbEdit.isChecked = false
            holder.tvCountCategory.text = "${holder.data!!.visitCount}번 방문했어요"
        }
        else{
            // 글자크기 조정
            val layoutParams = holder.layoutData.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 1.5f

            holder.cbEdit.visibility = View.GONE
            if(!holder.data!!.serverCategory.isNullOrBlank()){
                holder.tvCountCategory.text = holder.data!!.serverCategory!!
            }
            else {
                holder.tvCountCategory.text = " "
            }

            holder.cvStore.setOnClickListener {
                Log.d("store show", "store name : ${holder.data!!.storeName}")
            }

        }

        /**
         * 체크박스 클릭리스너
         */
        holder.cbEdit.setOnCheckedChangeListener { buttonView, isChecked ->

        }

    }
}