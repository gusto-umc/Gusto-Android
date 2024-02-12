package com.gst.gusto.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.mapUtil


class RouteViewPagerAdapter(private val itemList: List<mapUtil.Companion.MarkerItem>,val activity: MainActivity) : RecyclerView.Adapter<RouteViewPagerAdapter.ReviewDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_map_route_vp, parent, false)
        return ReviewDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewDetailViewHolder, position: Int) {
        val item = itemList[position]
        holder.tv_route_order.text = "${position+1}"
        holder.tv_rest_name.text = item.storeName
        holder.tv_rest_loc.text = item.address
        holder.btn_detail.setOnClickListener {
            activity.getCon().navigate(R.id.action_groupMRoutMapFragment_to_storeDetailFragment)
        }
        //holder.btn_bookmark
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

    }
}
