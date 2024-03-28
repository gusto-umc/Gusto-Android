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


class FollowListAdapter(val itemList: MutableList<Member>, val fragment : MyFollowListFragment):
    RecyclerView.Adapter<FollowListAdapter.FollowListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_follow_view, parent, false)
        return FollowListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowListViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.tv_name.text = currentItem.nickname
        setImage(holder.iv_img,itemList[position].profileImg,holder.itemView.context)
        holder.itemView.setOnClickListener {
            if(fragment.gustoViewModel.userNickname == itemList[position].nickname) {
                fragment.findNavController().navigate(R.id.action_followList_to_myFragment)
            } else {
                fragment.gustoViewModel.currentFeedNickname = itemList[position].nickname
                fragment.findNavController().navigate(R.id.action_followList_to_otherFragment)
            }
        }
    }
    fun addItems(newItems: List<Member>) {
        val startPosition = itemList.size // 기존 아이템 리스트의 마지막 인덱스 + 1
        itemList.addAll(newItems) // 새로운 아이템들을 기존 리스트에 추가
        notifyItemRangeInserted(startPosition, newItems.size) // 아이템 추가를 RecyclerView에 알림
    }


    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class FollowListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val iv_img = itemView.findViewById<ImageView>(R.id.iv_img)
    }


}

