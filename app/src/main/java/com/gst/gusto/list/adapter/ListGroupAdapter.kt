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

data class GroupItem (val title : String, val people : Int, val food : Int, val route : Int)

class ListGroupAdapter(val itemList: ArrayList<GroupItem>, val nc: NavController,val option : Int):
    RecyclerView.Adapter<ListGroupAdapter.ListGroupViewHolder>(){

    val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_group_view, parent, false)

        if(option==1) {
            view.findViewById<ImageView>(R.id.iv_icon).setImageResource(R.drawable.route_img)
        }
        return ListGroupViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "ResourceAsColor")
    override fun onBindViewHolder(holder: ListGroupViewHolder, position: Int) {
        holder.tv_title.text = itemList[position].title
        if(option == 1) {
            holder.tv_food.text = "장소 : ${itemList[position].food}개"
            if(itemList[position].route==0)
                holder.tv_route.text = "그룹 : X"
            else
                holder.tv_route.text = "그룹 : O"
        } else {
            holder.tv_people.text = "${itemList[position].people}명"
            holder.tv_food.text = "맛집 : ${itemList[position].food}개"
            holder.tv_route.text = "루트 : ${itemList[position].route}개"
        }

        holder.item.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 버튼을 누를 때 처
                    ViewCompat.setBackgroundTintList(holder.item, null)
                    holder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.tv_route.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.btn.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                }
                MotionEvent.ACTION_UP -> {
                    // 버튼에서 손을 뗄 때 처리
                    ViewCompat.setBackgroundTintList(holder.item, colorStateOnList)
                    holder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    holder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.tv_route.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    holder.btn.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                }
                MotionEvent.ACTION_CANCEL -> {
                    // 버튼에서 손을 뗄 때 처리
                    ViewCompat.setBackgroundTintList(holder.item, colorStateOnList)
                    holder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    holder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.tv_route.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    holder.btn.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                }

            }
            // true를 반환하여 이벤트 소비
             false
        }
        
        holder.item.setOnClickListener {
            nc.navigate(R.id.action_listFragment_to_groupFragment)
        }

    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ListGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title_group)
        val tv_people = itemView.findViewById<TextView>(R.id.tv_member_num)
        val tv_food = itemView.findViewById<TextView>(R.id.tv_food_num)
        val tv_route = itemView.findViewById<TextView>(R.id.tv_route_num)
        val item = itemView.findViewById<LinearLayout>(R.id.item_list_group)
        val icon = itemView.findViewById<ImageView>(R.id.iv_icon)
        val btn = itemView.findViewById<TextView>(R.id.btn_tmp)
    }

}

