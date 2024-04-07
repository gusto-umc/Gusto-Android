package com.gst.gusto.my.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gst.clock.Fragment.MyFollowListFragment
import com.gst.gusto.R
import com.gst.gusto.api.Member
import com.gst.gusto.util.util.Companion.setImage


class FollowListAdapter(val itemList: MutableList<Member?>, val fragment : MyFollowListFragment):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
    }
    override fun getItemViewType(position: Int): Int {
        return when (itemList.get(position)) {
            null -> TYPE_LOADING
            else -> TYPE_ITEM

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_follow_view, parent, false)
                FollowListViewHolder(view)
            }
            TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buffering, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_ITEM -> {
                val currentItem = itemList[position]
                if(currentItem!=null) {
                    val viewHolder = holder as FollowListViewHolder
                    viewHolder.tv_name.text = currentItem.nickname
                    setImage(viewHolder.iv_img, currentItem.profileImg, holder.itemView.context)
                    viewHolder.itemView.setOnClickListener {
                        if(fragment.gustoViewModel.userNickname == currentItem.nickname) {
                            fragment.findNavController().navigate(R.id.action_followList_to_myFragment)
                        } else {
                            fragment.gustoViewModel.currentFeedNickname = currentItem.nickname
                            fragment.findNavController().navigate(R.id.action_followList_to_otherFragment)
                        }
                    }
                }
            }
            TYPE_LOADING -> {
                // Do nothing for loading view
            }
        }
    }
    fun addItems(newItems: List<Member>) {
        removeLastItem()
        val startPosition = itemList.size // 기존 아이템 리스트의 마지막 인덱스 + 1
        itemList.addAll(newItems) // 새로운 아이템들을 기존 리스트에 추가
        addLoading()
        notifyItemRangeInserted(startPosition, newItems.size+1) // 아이템 추가를 RecyclerView에 알림
    }
    fun addLoading() {
        itemList.add(null)
        notifyItemInserted(itemList.size - 1 ) // 아이템 추가를 RecyclerView에 알림
    }
    fun removeLastItem() {
        if (itemList.isNotEmpty()) {
            val lastPosition = itemList.size - 1 // 마지막 아이템의 위치
            itemList.removeAt(lastPosition) // 마지막 아이템 제거
            notifyItemRemoved(lastPosition) // 아이템 삭제를 RecyclerView에 알림
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class FollowListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val iv_img = itemView.findViewById<ImageView>(R.id.iv_img)
    }
    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

