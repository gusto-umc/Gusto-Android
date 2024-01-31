package com.gst.gusto.list.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R


class MapRoutesAdapter(val itemList: ArrayList<RouteItem>, val lyAddRoute: ConstraintLayout):
    RecyclerView.Adapter<MapRoutesAdapter.ListViewHolder>(){

    val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#A6A6A6"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_map_route_view, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.tv_rest_name.setText(itemList[position].name)
        holder.tv_route_order.text = (position+1).toString()
        if(position==0) holder.iv_line_up.visibility = View.INVISIBLE
        if(lyAddRoute.visibility == View.INVISIBLE) holder.iv_line_down.visibility = View.INVISIBLE
        else holder.iv_line_down.visibility = View.VISIBLE

        holder.tv_rest_name.setOnClickListener {
            holder.ly_rest_name.backgroundTintList = colorStateOnList
            holder.et_rest_name.setText(itemList[position].name)
            holder.tv_rest_name.visibility = View.GONE
            holder.btn_remove.visibility = View.VISIBLE
        }
        holder.btn_remove.setOnClickListener {
            itemList.removeAt(position)
            lyAddRoute.visibility = View.VISIBLE
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,getItemCount())
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_rest_name = itemView.findViewById<TextView>(R.id.tv_rest_name)
        val et_rest_name = itemView.findViewById<EditText>(R.id.et_rest_name)
        val tv_route_order = itemView.findViewById<TextView>(R.id.tv_route_order)
        val iv_line_up = itemView.findViewById<ImageView>(R.id.iv_line_up)
        val iv_line_down = itemView.findViewById<ImageView>(R.id.iv_line_down)
        val ly_rest_name = itemView.findViewById<LinearLayout>(R.id.ly_rest_name)
        val btn_remove = itemView.findViewById<ImageView>(R.id.btn_remove)
    }

}

