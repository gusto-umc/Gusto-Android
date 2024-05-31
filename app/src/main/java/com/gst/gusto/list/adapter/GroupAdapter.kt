package com.gst.gusto.list.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.util.util.Companion.setPopupTwo
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.Member
import com.gst.gusto.list.fragment.GroupStoresFragment
import com.gst.gusto.my.adapter.FollowListAdapter

data class RestItem(
    val storeName: String,
    val address: String,
    val storeProfileImg: String,
    val userProfileImg: String,
    val storeId: Long,
    val groupListId: Long
)

class GroupAdapter(val itemList: MutableList<RestItem?>,val gustoViewModel: GustoViewModel,val groupStoresFragment: GroupStoresFragment):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_view, parent, false)
                GroupViewHolder(view)
            }
            TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buffering, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_ITEM -> {
                holder as GroupViewHolder
                val currentItem = itemList[position]
                if(currentItem!=null) {
                    holder.tv_rest_name.text = currentItem.storeName
                    holder.tv_rest_location.text = currentItem.address
                    setImage(holder.iv_recomend_profile_image,currentItem.userProfileImg,holder.itemView.context)
                    setImage(holder.iv_rest,currentItem.storeProfileImg,holder.itemView.context)
                    holder.btn_detail.setOnClickListener {
                        gustoViewModel.selectedDetailStoreId = currentItem.storeId.toInt()
                        findNavController(holder.itemView).navigate(R.id.action_groupFragment_to_storeDetailFragment)
                    }
                    holder.itemView.setOnLongClickListener {
                        setPopupTwo(holder.itemView.context, "${currentItem.storeName}을\n그룹 맛집에서 삭제하시겠습니까?", "", 1) { option ->
                            when (option) {
                                0 -> {
                                    gustoViewModel.deleteGroupStore(currentItem.groupListId) { result ->
                                        when (result) {
                                            1 -> {
                                                itemList.remove(currentItem)
                                                notifyDataSetChanged()
                                                //groupStoresFragment.checkGroupStores()
                                            }
                                            else -> {
                                                Toast.makeText(holder.itemView.context, "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        false
                    }
                }

            }
        }
    }
    fun addItems(newItems: List<RestItem>) {
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

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_rest_name = itemView.findViewById<TextView>(R.id.tv_rest_name)
        val tv_rest_location = itemView.findViewById<TextView>(R.id.tv_rest_location)
        val iv_recomend_profile_image = itemView.findViewById<ImageView>(R.id.iv_recomend_profile_image)
        val iv_rest = itemView.findViewById<ImageView>(R.id.iv_rest)
        val btn_detail = itemView.findViewById<ImageView>(R.id.btn_detail)
    }
    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

