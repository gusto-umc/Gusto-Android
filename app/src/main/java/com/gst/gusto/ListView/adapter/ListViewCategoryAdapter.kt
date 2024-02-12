package com.gst.gusto.ListView.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.CategoryDetail
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.R
import com.gst.gusto.databinding.ItemListviewCategoryShowBinding

class ListViewCategoryAdapter(private var flag : String, private val fragmentManager : FragmentManager, private val parentView : View) : ListAdapter<CategorySimple, ListViewCategoryAdapter.ViewHolder>(
    DiffCallback) {

    private val mFragmentManager = fragmentManager

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


    inner class ViewHolder(private val binding : ItemListviewCategoryShowBinding) : RecyclerView.ViewHolder(binding.root){
        var data : CategorySimple? = null

        fun bind(simple: CategorySimple){
            data = simple
            binding.ivItemCategoryShow.setImageResource(R.drawable.category_icon_1)
            binding.tvItemCategoryShowTitle.text = simple.categoryName
            binding.tvCategoryShowCount.text = "${simple.storeCount}개"
        }
        val updownLayout = binding.layoutItemCategoryShowUpdown
        val storeRv = binding.rvItemCategoryShowStore
        val ivUpDown = binding.btnItemCategoryShowDown
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemListviewCategoryShowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var openFlag = false
        val cateId = holder.bind(getItem(position))

        //펼치기 클릭리스너
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
                        Store(id = 0, storeName = "구스토 레스토랑", location = "메롱시 메로나동 바밤바 24-6 3층", visitCount = 3, storePhoto = 1, serverCategory = "양식", isSaved = null),
                        Store(id = 1, storeName = "Gusto Restaurant", location = "메롱시 메로나동 바밤바 24-6 1층", visitCount = 7, storePhoto = 1, serverCategory = "일식", isSaved = null)
                    )
                    val mStoreAdapter = ListViewStoreAdapter(flag, parentView)
                    mStoreAdapter.submitList(sampleStoreData!!)
                    holder.storeRv.adapter = mStoreAdapter
                    holder.storeRv.layoutManager = LinearLayoutManager(holder.storeRv.context, LinearLayoutManager.VERTICAL, false)
                }
            }
            else {

            }
        }

        //개수에 따라 펼치기 활성 상태 변경
        if(holder.data?.storeCount!! <= 0) {
            holder.ivUpDown.imageTintList = ColorStateList.valueOf(Color.parseColor("#ECECEC"))
        }

        // route인 경우 롱클릭 비활성화
        if(flag != "route"){
            holder.updownLayout.setOnLongClickListener {
                val categoryBottomSheetDialog = CategoryBottomSheetDialog(){
                    when(it){
                        0 -> {
                            Log.d("bottomsheet", "저장 click")
                        }
                    }
                }
                categoryBottomSheetDialog.isAdd = false
                categoryBottomSheetDialog.categoryEdiBottomSheetData = CategoryDetail(id = holder.data!!.id, categoryName = holder.data!!.categoryName, categoryDesc = "냠냠", categoryIcon = 1, isPublic = true )
                categoryBottomSheetDialog.show(mFragmentManager, categoryBottomSheetDialog.tag)
                true
            }
        }




    }

}