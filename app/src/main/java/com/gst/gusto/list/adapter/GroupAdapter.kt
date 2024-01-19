package com.gst.gusto.list.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R

data class RestItem (val name : String, val loc : String, val rest : Int, val recomend : Int)

class GroupAdapter(val itemList: ArrayList<RestItem>):
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>(){

    val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_view, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.tv_rest_name.text = itemList[position].name
        holder.tv_rest_location.text = itemList[position].loc
        holder.iv_recomend_profile_image.setImageResource(itemList[position].recomend)
        holder.iv_rest.setImageResource(itemList[position].rest)
        holder.btn_detail.setOnClickListener {
            //nc.navigate(R.id.action_listFragment_to_groupFragment)
        }

    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_rest_name = itemView.findViewById<TextView>(R.id.tv_rest_name)
        val tv_rest_location = itemView.findViewById<TextView>(R.id.tv_rest_location)
        val iv_recomend_profile_image = itemView.findViewById<ImageView>(R.id.iv_recomend_profile_image)
        val iv_rest = itemView.findViewById<ImageView>(R.id.iv_rest)
        val btn_detail = itemView.findViewById<ImageView>(R.id.btn_detail)
    }

}

