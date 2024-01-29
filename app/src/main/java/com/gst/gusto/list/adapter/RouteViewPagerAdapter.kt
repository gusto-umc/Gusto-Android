package com.gst.gusto.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R

data class RouteMapDetailItem (val name : String, val loc : String, val clock : String, val phone : String, val bookmark : Boolean)

class RouteViewPagerAdapter(private val itemList: List<RouteMapDetailItem>) : RecyclerView.Adapter<RouteViewPagerAdapter.ReviewDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_map_route_vp, parent, false)
        return ReviewDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewDetailViewHolder, position: Int) {
        val item = itemList[position]
        holder.tv_route_order.text = "${position+1}"
        holder.tv_rest_name.text = item.name
        holder.tv_rest_loc.text = item.loc
        holder.btn_detail.setOnClickListener {

        }
        //holder.btn_bookmark
        holder.tv_rest_clock.text = item.clock
        holder.tv_rest_phone.text = item.phone
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ReviewDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_route_order: TextView = itemView.findViewById(R.id.tv_route_order)
        val tv_rest_name: TextView = itemView.findViewById(R.id.tv_rest_name)
        val btn_detail: ImageView = itemView.findViewById(R.id.btn_detail)
        val btn_bookmark: ImageView = itemView.findViewById(R.id.btn_bookmark)
        val tv_rest_loc: TextView = itemView.findViewById(R.id.tv_rest_loc)
        val tv_rest_clock: TextView = itemView.findViewById(R.id.tv_rest_clock)
        val tv_rest_phone: TextView = itemView.findViewById(R.id.tv_rest_phone)

    }
}
