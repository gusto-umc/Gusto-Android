package com.gst.gusto.ListView.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.R
import com.gst.gusto.databinding.ItemListviewCategoryEditBinding

class ListViewEditCategoryAdapter (private var flag : String) : ListAdapter<CategorySimple, ListViewEditCategoryAdapter.ViewHolder>(
    DiffCallback) {



    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CategorySimple>(){
            override fun areItemsTheSame(oldItem: CategorySimple, newItem: CategorySimple): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CategorySimple, newItem: CategorySimple): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }


    inner class ViewHolder(private val binding : ItemListviewCategoryEditBinding) : RecyclerView.ViewHolder(binding.root){
        var data : CategorySimple? = null

        fun bind(simple: CategorySimple){
            data = simple
            binding.ivItemCategoryEdit.setImageResource(R.drawable.category_icon_1)
            binding.tvItemCategoryEditTitle.text = simple.categoryName
            binding.tvItemCategoryEditCount.text = "${simple.storeCount}개"
        }
        val updownLayout = binding.layoutItemCategoryEditUpdown
        val storeRv = binding.rvItemCategoryShowStore
        val ivUpDown = binding.btnItemCategoryEditDown
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemListviewCategoryEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var openFlag = false
        val cateId = holder.bind(getItem(position))

        holder.updownLayout.setOnClickListener {
            if(holder.data?.storeCount != 0){
                if(openFlag){
                    holder.storeRv.visibility = View.GONE
                    holder.ivUpDown.setImageResource(R.drawable.arrow_down_2_img)
                    openFlag = false
                }
                else{
                    holder.storeRv.visibility = View.VISIBLE
                    holder.ivUpDown.setImageResource(R.drawable.arrow_up_1_img)
                    openFlag = true
                    /**
                     * storeRv 연결
                     */
                    //서버 연결 예정
                    var sampleStoreData : MutableList<Store> = arrayListOf<Store>(
                        Store(id = 0, storeName = "구스토 레스토랑", location = "메롱시 메로나동 바밤바 24-6 3층", visitCount = 3, storePhoto = 1, serverCategory = null, isSaved = null),
                        Store(id = 1, storeName = "Gusto Restaurant", location = "메롱시 메로나동 바밤바 24-6 1층", visitCount = 7, storePhoto = 1, serverCategory = null, isSaved = null)
                    )
                    val mStoreAdapter = ListViewStoreAdapter("edit")
                    mStoreAdapter.submitList(sampleStoreData!!)
                    holder.storeRv.adapter = mStoreAdapter
                    holder.storeRv.layoutManager = LinearLayoutManager(holder.storeRv.context, LinearLayoutManager.VERTICAL, false)
                }
            }
            else {

            }
        }

        if(holder.data?.storeCount!! <= 0) {
            Log.d("0개", "체")
            holder.ivUpDown.imageTintList = ColorStateList.valueOf(Color.parseColor("#ECECEC"))
        }




    }

}