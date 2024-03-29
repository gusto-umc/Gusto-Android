package com.gst.gusto.list.adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.util.mapUtil


class MapRoutesAdapter(
    val itemList: ArrayList<mapUtil.Companion.MarkerItem>, val lyAddRoute: ConstraintLayout,
    val activity: Activity?, val option : Int):RecyclerView.Adapter<MapRoutesAdapter.ListViewHolder>(){
// option 0(루트 화면), 1(마이 화면), 2(다른 유저 프로필 화면)
    val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#A6A6A6"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_map_route_view, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val parentActivity = activity as MainActivity
        holder.tv_rest_name.setText(itemList[position].storeName)
        holder.tv_route_order.text = (position+1).toString()
        holder.tv_rest_loc.text = itemList[position].address
        if(position==0) holder.iv_line_up.visibility = View.INVISIBLE
        if(lyAddRoute.visibility == View.INVISIBLE) holder.iv_line_down.visibility = View.INVISIBLE
        else if(lyAddRoute.visibility == View.GONE && position == itemList.count()-1) holder.iv_line_down.visibility = View.INVISIBLE
        else holder.iv_line_down.visibility = View.VISIBLE

        if(lyAddRoute.visibility == View.VISIBLE) {
            holder.tv_rest_name.setOnClickListener {
                holder.ly_rest_name.backgroundTintList = colorStateOnList
                holder.et_rest_name.setText(itemList[position].storeName)
                holder.tv_rest_name.visibility = View.GONE
                holder.tv_rest_loc.visibility = View.GONE
                holder.btn_remove.visibility = View.VISIBLE
            }
            holder.btn_remove.setOnClickListener {
                lyAddRoute.visibility = View.VISIBLE
                if(option!=1) {
                    val gustoVM = parentActivity.gustoViewModel
                    if(gustoVM.addRoute.contains(itemList[position].storeId)) gustoVM.addRoute.remove(itemList[position].storeId)
                    else gustoVM.removeRoute.add(itemList[position].routeListId)
                    itemList.remove(itemList[position])
                    gustoVM.markerListLiveData.value = itemList
                }
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,getItemCount())
            }
        } else {
            if(option==0) {
                holder.itemView.setOnClickListener{
                    parentActivity.gustoViewModel.selectedDetailStoreId = itemList[position].storeId.toInt()
                    parentActivity.getCon().navigate(R.id.action_routeStoresFragment_to_storeDetailFragment)
                    parentActivity.getViewModel().groupFragment = 1
                }
                holder.tv_rest_name.setOnClickListener{
                    parentActivity.gustoViewModel.selectedDetailStoreId = itemList[position].storeId.toInt()
                    parentActivity.getCon().navigate(R.id.action_routeStoresFragment_to_storeDetailFragment)
                    parentActivity.getViewModel().groupFragment = 1
                }
            } else if(option==2) {
                holder.itemView.setOnClickListener{
                    parentActivity.gustoViewModel.selectedDetailStoreId = itemList[position].storeId.toInt()
                    parentActivity.getCon().navigate(R.id.action_fragment_other_to_storeDetailFragment)
                    parentActivity.getViewModel().groupFragment = 1
                }
                holder.tv_rest_name.setOnClickListener{
                    parentActivity.gustoViewModel.selectedDetailStoreId = itemList[position].storeId.toInt()
                    parentActivity.getCon().navigate(R.id.action_fragment_other_to_storeDetailFragment)
                    parentActivity.getViewModel().groupFragment = 1
                }
            } else {
                holder.itemView.setOnClickListener{
                    parentActivity.gustoViewModel.selectedDetailStoreId = itemList[position].storeId.toInt()
                    parentActivity.getCon().navigate(R.id.action_myFragment_to_storeDetailFragment)
                }
                holder.tv_rest_name.setOnClickListener{
                    parentActivity.gustoViewModel.selectedDetailStoreId = itemList[position].storeId.toInt()
                    parentActivity.getCon().navigate(R.id.action_myFragment_to_storeDetailFragment)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_rest_name = itemView.findViewById<TextView>(R.id.tv_rest_name)
        val et_rest_name = itemView.findViewById<TextView>(R.id.et_rest_name)
        val tv_rest_loc = itemView.findViewById<TextView>(R.id.tv_rest_loc)
        val tv_route_order = itemView.findViewById<TextView>(R.id.tv_route_order)
        val iv_line_up = itemView.findViewById<ImageView>(R.id.iv_line_up)
        val iv_line_down = itemView.findViewById<ImageView>(R.id.iv_line_down)
        val ly_rest_name = itemView.findViewById<LinearLayout>(R.id.ly_rest_name)
        val btn_remove = itemView.findViewById<ImageView>(R.id.btn_remove)
    }

}

