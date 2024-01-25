package com.gst.gusto.my.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.gst.gusto.R

data class FollowItem(val img: String, val name: String)

class FollowListAdapter(val itemList: ArrayList<FollowItem>):
    RecyclerView.Adapter<FollowListAdapter.FollowListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_follow_view, parent, false)
        return FollowListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowListViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.tv_name.text = currentItem.name
        //holder.iv_img.setImgae...
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class FollowListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val iv_img = itemView.findViewById<ImageView>(R.id.iv_img)
    }


}

