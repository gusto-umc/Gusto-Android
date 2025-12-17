package com.gst.gusto.my.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.clock.Fragment.ListRouteFragment
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.LisAdapter
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.fragment.GroupRouteRoutesFragment
import com.gst.gusto.util.util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class myRouteAdapter (val itemList: MutableList<GroupItem?>, val gustoViewModel: GustoViewModel, private val activity : Activity
):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
    }
    override fun getItemViewType(position: Int): Int {
        return when (itemList.get(position)) {
            null -> TYPE_LOADING
            else -> TYPE_ITEM

        }
    }

    val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_route2, parent, false)
                return ListGroupViewHolder(view)
            }
            TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buffering, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("ClickableViewAccessibility", "ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_ITEM -> {
                val viewHolder = holder as ListGroupViewHolder

                val currentItem = itemList[position]
                if(currentItem!=null) {
                    viewHolder.tv_title.text = currentItem.groupName
                    viewHolder.tv_food.text = "장소 : ${currentItem.numRestaurants}개"

                    viewHolder.item.setOnTouchListener { view, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                // 버튼을 누를 때 처리
                                ViewCompat.setBackgroundTintList(holder.item, null)
                                viewHolder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                                viewHolder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                                viewHolder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                                viewHolder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.white))
                            }
                            MotionEvent.ACTION_UP -> {
                                // 버튼에서 손을 뗄 때 처리
                                ViewCompat.setBackgroundTintList(holder.item, colorStateOnList)
                                viewHolder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                                viewHolder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                // 버튼에서 손을 뗄 때 처리
                                ViewCompat.setBackgroundTintList(holder.item, colorStateOnList)
                                viewHolder.tv_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                                viewHolder.tv_people.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.tv_food.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
                                viewHolder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
                            }

                        }
                        // true를 반환하여 이벤트 소비
                        false
                    }
                    viewHolder.item.setOnClickListener {
                        viewHolder.recyclerView.visibility =
                            if (viewHolder.recyclerView.visibility == View.VISIBLE)
                                View.GONE
                            else
                                View.VISIBLE

                        gustoViewModel.getGroupRouteDetail(currentItem.groupId) { result ->
                            when (result) {
                                1 -> {
                                    var itemList = gustoViewModel.markerListLiveData.value!!
                                    val nickname = gustoViewModel.profileNickname
                                    if(nickname!="") {
                                        val boardAdapter = MapRoutesAdapter(itemList,viewHolder.ly_gone,activity,2)
                                        boardAdapter.notifyDataSetChanged()

                                        viewHolder.recyclerView.adapter = boardAdapter
                                        viewHolder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
                                    } else {
                                        val boardAdapter = MapRoutesAdapter(itemList,viewHolder.ly_gone,activity,1)
                                        boardAdapter.notifyDataSetChanged()

                                        viewHolder.recyclerView.adapter = boardAdapter
                                        viewHolder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
                                    }
                                }
                                else -> {
                                    Toast.makeText(holder.itemView.context,"서버와의 연결 불안정",
                                        Toast.LENGTH_SHORT ).show()
                                }
                            }
                        }

                    }

                }

            }
            TYPE_LOADING -> {

            }
        }




    }

    override fun getItemCount(): Int {
        return itemList.count()
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
    fun addItems(newItems: List<GroupItem>) {
        removeLastItem()
        val startPosition = itemList.size // 기존 아이템 리스트의 마지막 인덱스 + 1
        itemList.addAll(newItems) // 새로운 아이템들을 기존 리스트에 추가
        addLoading()
        notifyItemRangeInserted(startPosition, newItems.size+1) // 아이템 추가를 RecyclerView에 알림
    }
    inner class ListGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title_group)
        val tv_people = itemView.findViewById<TextView>(R.id.tv_member_num)
        val tv_food = itemView.findViewById<TextView>(R.id.tv_food_num)
        val item = itemView.findViewById<LinearLayout>(R.id.item_list_group)
        val icon = itemView.findViewById<ImageView>(R.id.iv_icon)
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerView)
        val ly_gone = itemView.findViewById<ConstraintLayout>(R.id.ly_gone)
    }
    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}