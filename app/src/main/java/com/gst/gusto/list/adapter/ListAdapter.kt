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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gst.clock.Fragment.ListRouteFragment
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.Member
import com.gst.gusto.list.fragment.GroupRouteRoutesFragment
import com.gst.gusto.my.adapter.FollowListAdapter
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
    val itemList: MutableList<GroupItem?>, val nc: NavController?,val option: Int, val gustoViewModel: GustoViewModel, val fragment : Fragment?
):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
    }
    override fun getItemViewType(position: Int): Int {
        return when (itemList.get(position)) {
            null -> LisAdapter.TYPE_LOADING
            else -> LisAdapter.TYPE_ITEM

        }
    }

    val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return when (viewType) {
            LisAdapter.TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_group_view, parent, false)

                // 디폴트 리스트 그룹
                if(option==1||option==2||option==3) {
                    view.findViewById<ImageView>(R.id.iv_icon).setImageResource(R.drawable.route_img)
                }
                if(option==3) { // 마이 루트
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_route, parent, false)
                    return ListGroupViewHolder(view)
                }
                ListGroupViewHolder(view)
            }
            LisAdapter.TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buffering, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    @SuppressLint("ClickableViewAccessibility", "ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            LisAdapter.TYPE_ITEM -> {
                val viewHolder = holder as ListGroupViewHolder

                val currentItem = itemList[position]
                if(currentItem!=null) {
                    viewHolder.tv_title.text = currentItem.groupName
                    if(option == 1||option==2||option==3) {
                        viewHolder.tv_food.text = "장소 : ${currentItem.numRestaurants}개"
                        viewHolder.ly_route.visibility = View.GONE
                    } else if(option==0){
                        viewHolder.tv_people.text = "${currentItem.numMembers}명"
                        viewHolder.tv_food.text = "맛집 : ${currentItem.numRestaurants}개"
                        viewHolder.tv_route.text = "루트 : ${currentItem.numRoutes}개"
                        viewHolder.btn_remove.visibility = View.GONE
                    }
                    if(option==2) viewHolder.btn_remove.visibility = View.VISIBLE
                    viewHolder.item.setOnTouchListener { view, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                // 버튼을 누를 때 처리
                                ViewCompat.setBackgroundTintList(holder.item, null)
                                viewHolder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                                viewHolder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                                viewHolder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                                viewHolder.tv_route.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                                viewHolder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.white))
                                viewHolder.btn_remove.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                            }
                            MotionEvent.ACTION_UP -> {
                                // 버튼에서 손을 뗄 때 처리
                                ViewCompat.setBackgroundTintList(holder.item, colorStateOnList)
                                viewHolder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                                viewHolder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.tv_route.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
                                viewHolder.btn_remove.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                // 버튼에서 손을 뗄 때 처리
                                ViewCompat.setBackgroundTintList(holder.item, colorStateOnList)
                                viewHolder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                                viewHolder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.tv_route.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
                                viewHolder.btn_remove.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                            }

                        }
                        // true를 반환하여 이벤트 소비
                        false
                    }
                    viewHolder.item.setOnClickListener {
                        if(option==0) {
                            // 그룹 리스트 들어가기
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(50)
                                gustoViewModel.currentGroupId = currentItem.groupId
                                gustoViewModel.groupOner = currentItem.isOner
                                nc?.navigate(R.id.action_listFragment_to_groupFragment)
                            }
                        } else if(option == 1){
                            // 루트 리스트 들어가기
                            gustoViewModel.getGroupRouteDetail(currentItem.groupId) { result ->
                                when (result) {
                                    1 -> {
                                        gustoViewModel.currentRouteId = currentItem.groupId
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
                            // 그룹에서 루트 리스트 들어가기
                            gustoViewModel.getGroupRouteDetail(currentItem.groupId) { result ->
                                when (result) {
                                    1 -> {
                                        gustoViewModel.currentRouteId = currentItem.groupId
                                        Navigation.findNavController(holder.itemView).navigate(R.id.action_groupMRSFragment_to_groupMRRFragment)
                                    }
                                    else -> {
                                        Toast.makeText(holder.itemView.context,"서버와의 연결 불안정",Toast.LENGTH_SHORT ).show()
                                    }
                                }
                            }

                        } else if(option == 3){
                            // 마이에서 루트 리스트 들어가기
                            val nickname = gustoViewModel.profileNickname
                            Log.d("viewmodel","nickname ${nickname}")
                            if(nickname=="") {
                                gustoViewModel.getGroupRouteDetail(currentItem.groupId) { result ->
                                    when (result) {
                                        1 -> {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                delay(50)
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    delay(50)
                                                    Navigation.findNavController(holder.itemView).navigate(R.id.action_myRouteRoutesFragment_to_myRouteStoresFragment)
                                                }
                                            }
                                        }
                                        else -> {
                                            Toast.makeText(holder.itemView.context,"서버와의 연결 불안정",Toast.LENGTH_SHORT ).show()
                                        }
                                    }
                                }
                            } else {
                                gustoViewModel.getOtherRouteDetail(currentItem.groupId,nickname) { result ->
                                    when (result) {
                                        1 -> {
                                            Log.d("viewmodel","other view clear")
                                            CoroutineScope(Dispatchers.Main).launch {
                                                delay(50)
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    delay(50)
                                                    Navigation.findNavController(holder.itemView).navigate(R.id.action_myRouteRoutesFragment_to_myRouteStoresFragment)
                                                }
                                            }
                                        }
                                        else -> {
                                            Toast.makeText(holder.itemView.context,"서버와의 연결 불안정",Toast.LENGTH_SHORT ).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    holder.btn_remove.setOnClickListener {
                        var item = itemList[position]
                        if(item!=null) {
                            util.setPopupTwo(holder.itemView.context,"${item.groupName}을\n내 루트에서 삭제하시겠습니까?","",1) { yesOrNo ->
                                when (yesOrNo) {
                                    0 -> {
                                        if(option==1) {
                                            gustoViewModel.deleteRoute(item.groupId) { result ->
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
                                            gustoViewModel.removeGroupRoute(item.groupId) { result ->
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
                }
            }
            LisAdapter.TYPE_LOADING -> {
                // Do nothing for loading view
            }
        }


    }

    override fun getItemCount(): Int {
        return itemList.count()
    }
    fun addItems(newItems: List<GroupItem>) {
        removeLastItem()
        val startPosition = itemList.size // 기존 아이템 리스트의 마지막 인덱스 + 1
        itemList.addAll(newItems) // 새로운 아이템들을 기존 리스트에 추가
        addLoading()
        notifyItemRangeInserted(startPosition, newItems.size+1) // 아이템 추가를 RecyclerView에 알림
    }
    fun addLoading() {
        itemList.add(null)
        notifyItemInserted(itemList.size - 1 ) // 아이템 추가를 RecyclerView에 알림
    }
    fun removeLastItem() {
        if (itemList.isNotEmpty()) {
            val lastPosition = itemList.size - 1 // 마지막 아이템의 위치
            itemList.removeAt(lastPosition) // 마지막 아이템 제거
            notifyItemRemoved(lastPosition) // 아이템 삭제를 RecyclerView에 알림
        }
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
    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

