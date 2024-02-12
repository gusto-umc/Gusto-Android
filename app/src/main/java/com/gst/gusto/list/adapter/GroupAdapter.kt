package com.gst.gusto.list.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.setImage

data class RestItem(
    val storeName: String,
    val address: String,
    val storeProfileImg: String,
    val userProfileImg: String,
    val storeId: Long,
    val groupListId: Int
)

class GroupAdapter(val itemList: ArrayList<RestItem>):
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>(){

    val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_view, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.tv_rest_name.text = itemList[position].storeName
        holder.tv_rest_location.text = itemList[position].address
        setImage(holder.iv_recomend_profile_image,itemList[position].userProfileImg,holder.itemView.context)
        setImage(holder.iv_rest,itemList[position].storeProfileImg,holder.itemView.context)
        holder.btn_detail.setOnClickListener {
            findNavController(holder.itemView).navigate(R.id.action_groupFragment_to_storeDetailFragment)
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

