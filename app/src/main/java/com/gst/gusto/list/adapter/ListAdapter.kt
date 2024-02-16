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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.gst.clock.Fragment.ListRouteFragment
import com.gst.gusto.R
import com.gst.gusto.Util.mapUtil.Companion.MarkerItem
import com.gst.gusto.Util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.list.fragment.GroupRouteRoutesFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class GroupItem(
    val groupId: Long,
    val groupName: String,
    val numMembers: Int,
    val isOner:Boolean,
    val numRestaurants: Int,
    val numRoutes: Int
)

class LisAdapter(
    val itemList: ArrayList<GroupItem>, val nc: NavController?,val option: Int, val gustoViewModel: GustoViewModel, val fragment : Fragment?
):RecyclerView.Adapter<LisAdapter.ListGroupViewHolder>(){

    val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_group_view, parent, false)

        // 디폴트 리스트 그룹
        if(option==1||option==2||option==3) {
            view.findViewById<ImageView>(R.id.iv_icon).setImageResource(R.drawable.route_img)
        }
        if(option==3) { // 마이 루트
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_route, parent, false)
            return ListGroupViewHolder(view)
        }
        return ListGroupViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "ResourceAsColor")
    override fun onBindViewHolder(holder: ListGroupViewHolder, position: Int) {
        holder.tv_title.text = itemList[position].groupName
        if(option == 1||option==2||option==3) {
            holder.tv_food.text = "장소 : ${itemList[position].numRestaurants}개"
            holder.ly_route.visibility = View.GONE
        } else if(option==0){
            holder.tv_people.text = "${itemList[position].numMembers}명"
            holder.tv_food.text = "맛집 : ${itemList[position].numRestaurants}개"
            holder.tv_route.text = "루트 : ${itemList[position].numRoutes}개"
        }
        if(option==0 || option==3) holder.btn_remove.visibility = View.GONE

        holder.item.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 버튼을 누를 때 처리
                    ViewCompat.setBackgroundTintList(holder.item, null)
                    holder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.tv_route.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.btn_remove.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                }
                MotionEvent.ACTION_UP -> {
                    // 버튼에서 손을 뗄 때 처리
                    ViewCompat.setBackgroundTintList(holder.item, colorStateOnList)
                    holder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    holder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.tv_route.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    holder.btn_remove.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                }
                MotionEvent.ACTION_CANCEL -> {
                    // 버튼에서 손을 뗄 때 처리
                    ViewCompat.setBackgroundTintList(holder.item, colorStateOnList)
                    holder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    holder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.tv_route.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                    holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    holder.btn_remove.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                }

            }
            // true를 반환하여 이벤트 소비
             false
        }
        holder.item.setOnClickListener {
            if(option==0) {
                // 그룹 리스트 들어가기
                CoroutineScope(Dispatchers.Main).launch {
                    delay(50)
                    gustoViewModel.currentGroupId = itemList[position].groupId
                    gustoViewModel.groupOner = itemList[position].isOner
                    nc?.navigate(R.id.action_listFragment_to_groupFragment)
                }
            } else if(option == 1){
                gustoViewModel.getGroupRouteDetail(itemList[position].groupId) { result ->
                    when (result) {
                        1 -> {
                            gustoViewModel.currentRouteId = itemList[position].groupId
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(50)
                                nc?.navigate(R.id.action_listFragment_to_routeStoresFragment)
                            }
                        }
                        else -> {
                            Toast.makeText(holder.itemView.context,"서버와의 연결 불안정",Toast.LENGTH_SHORT ).show()
                        }
                    }
                }
            } else if(option == 2){
                gustoViewModel.getGroupRouteDetail(itemList[position].groupId) { result ->
                    when (result) {
                        1 -> {
                            gustoViewModel.currentRouteId = itemList[position].groupId
                            Navigation.findNavController(holder.itemView).navigate(R.id.action_groupMRSFragment_to_groupMRRFragment)
                        }
                        else -> {
                            Toast.makeText(holder.itemView.context,"서버와의 연결 불안정",Toast.LENGTH_SHORT ).show()
                        }
                    }
                }

            } else if(option == 3){
                CoroutineScope(Dispatchers.Main).launch {
                    delay(50)
                    Navigation.findNavController(holder.itemView).navigate(R.id.action_myRouteRoutesFragment_to_myRouteStoresFragment)
                }
            }
        }
        holder.btn_remove.setOnClickListener {
            util.setPopupTwo(holder.itemView.context,"구스또레스토랑을\n그룹 맛집에서 삭제핫시겠습니까?","",1) { yesOrNo ->
                when (yesOrNo) {
                    0 -> {
                        if(option==1) {
                            gustoViewModel.deleteRoute(itemList[position].groupId) { result ->
                                when (result) {
                                    1 -> {
                                        (fragment as ListRouteFragment)?.checkRoutes()
                                    }
                                    else -> {
                                        Toast.makeText(holder.itemView.context,"서버와의 연결 불안정",Toast.LENGTH_SHORT ).show()
                                    }
                                }
                            }
                        } else if(option==2) {
                            gustoViewModel.removeGroupRoute(itemList[position].groupId) { result ->
                                when (result) {
                                    1 -> {
                                        (fragment as GroupRouteRoutesFragment)?.checkRoutes()
                                    }
                                    else -> {
                                        Toast.makeText(holder.itemView.context,"서버와의 연결 불안정",Toast.LENGTH_SHORT ).show()
                                    }
                                }
                            }
                        }

                    }
                }
            }
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
        val ly_route = itemView.findViewById<LinearLayout>(R.id.ly_route)
        val item = itemView.findViewById<LinearLayout>(R.id.item_list_group)
        val icon = itemView.findViewById<ImageView>(R.id.iv_icon)
        val btn_remove = itemView.findViewById<TextView>(R.id.btn_remove)
    }

}

