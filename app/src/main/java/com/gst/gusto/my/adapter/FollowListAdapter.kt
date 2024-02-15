package com.gst.gusto.my.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.Member


class FollowListAdapter(val itemList: List<Member>):
    RecyclerView.Adapter<FollowListAdapter.FollowListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_follow_view, parent, false)
        return FollowListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowListViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.tv_name.text = currentItem.nickname
        setImage(holder.iv_img,itemList[position].profileImg,holder.itemView.context)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class FollowListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val iv_img = itemView.findViewById<ImageView>(R.id.iv_img)
    }


}

