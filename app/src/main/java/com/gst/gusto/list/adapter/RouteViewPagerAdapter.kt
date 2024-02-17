package com.gst.gusto.list.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.Util.util.Companion.setImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

        activity.gustoViewModel.getStoreDetailQuick(item.storeId) { result,data ->
            when (result) {
                1 -> {
                    if(data!=null) {
                        if(data.reviewImg3!=null) {
                            if(data.reviewImg3.size>0) setImage(holder.iv_1,data.reviewImg3[0],holder.itemView.context)
                            if(data.reviewImg3.size>1) setImage(holder.iv_2,data.reviewImg3[1],holder.itemView.context)
                            if(data.reviewImg3.size>2) setImage(holder.iv_3,data.reviewImg3[2],holder.itemView.context)
                        }
                        if(data.pin) holder.btn_bookmark.setImageResource(R.drawable.vector_black)
                    }
                }
                else -> {
                    Toast.makeText(holder.itemView.context,"서버와의 연결 불안정", Toast.LENGTH_SHORT ).show()
                }
            }
        }
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
        val iv_1: ImageView = itemView.findViewById(R.id.iv_1)
        val iv_2: ImageView = itemView.findViewById(R.id.iv_2)
        val iv_3: ImageView = itemView.findViewById(R.id.iv_3)

    }
}
