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


class RouteAdapter(val itemList: ArrayList<mapUtil.Companion.MarkerItem>, val activity: MainActivity):
    RecyclerView.Adapter<RouteAdapter.ListViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route_view, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.tv_rest_name.text = itemList[position].storeName
        holder.tv_rest_location.text = itemList[position].address
        holder.tv_route_order.text = (position+1).toString()
        if(position==0) holder.iv_line_up.visibility = View.INVISIBLE
        if(position+1>=itemList.count()) holder.iv_line_down.visibility = View.INVISIBLE
        holder.itemView.setOnClickListener{
            activity.getViewModel().groupFragment = 1
            activity.gustoViewModel.selectedDetailStoreId = itemList[position].storeId.toInt()
            activity.getCon().navigate(R.id.action_groupFragment_to_storeDetailFragment)
        }

    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_rest_name = itemView.findViewById<TextView>(R.id.tv_rest_name)
        val tv_rest_location = itemView.findViewById<TextView>(R.id.tv_rest_loc)
        val iv_line_up = itemView.findViewById<ImageView>(R.id.iv_line_up)
        val iv_line_down = itemView.findViewById<ImageView>(R.id.iv_line_down)
        val tv_route_order = itemView.findViewById<TextView>(R.id.tv_route_order)
    }

}

