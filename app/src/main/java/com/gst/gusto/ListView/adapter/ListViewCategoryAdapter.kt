package com.gst.gusto.ListView.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.CategoryDetail
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseMapCategory
import com.gst.gusto.api.ResponseStoreListItem
import com.gst.gusto.databinding.ItemListviewCategoryShowBinding

class ListViewCategoryAdapter(private var flag : String, private val fragmentManager : FragmentManager, private val parentView : View) : ListAdapter<ResponseMapCategory, ListViewCategoryAdapter.ViewHolder>(
    DiffCallback) {

    private val mFragmentManager = fragmentManager
    var viewModel : GustoViewModel? = null

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ResponseMapCategory>(){
            override fun areItemsTheSame(oldItem: ResponseMapCategory, newItem: ResponseMapCategory): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.myCategoryId == newItem.myCategoryId
            }

            override fun areContentsTheSame(oldItem: ResponseMapCategory, newItem: ResponseMapCategory): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }


    inner class ViewHolder(private val binding : ItemListviewCategoryShowBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseMapCategory? = null

        fun bind(simple: ResponseMapCategory){
            data = simple
            binding.ivItemCategoryShow.setImageResource(R.drawable.category_icon_1)
            binding.tvItemCategoryShowTitle.text = simple.categoryName
            binding.tvCategoryShowCount.text = "${simple.pinCnt}개"
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
        Log.d("my check", holder.data!!.categoryName)

        //펼치기 클릭리스너
        holder.updownLayout.setOnClickListener {
            if(holder.data?.pinCnt != 0){
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
                    if(flag == "route"){
                        viewModel!!.getAllUserStores(holder.data!!.myCategoryId){
                            result ->
                            when(result){
                                0 -> {
                                    //success
                                    val mStoreAdapter = ListViewStoreAdapter(flag, parentView)
                                    mStoreAdapter.submitList(viewModel!!.myAllStoreList!!)
                                    mStoreAdapter.gustoViewModel = viewModel
                                    holder.storeRv.adapter = mStoreAdapter
                                    holder.storeRv.layoutManager = LinearLayoutManager(holder.storeRv.context, LinearLayoutManager.VERTICAL, false)
                                }
                                1 -> {
                                    //실패
                                    Log.d("store checking", "fail")
                                }
                            }
                        }
                    }
                    else if (flag == "my"){

                        viewModel!!.getAllUserStores(holder.data!!.myCategoryId){
                                result ->
                            when(result){
                                0 -> {
                                    //success
                                    val mStoreAdapter = ListViewStoreAdapter(flag, parentView)
                                    mStoreAdapter.submitList(viewModel!!.myAllStoreList!!)
                                    mStoreAdapter.gustoViewModel = viewModel
                                    holder.storeRv.adapter = mStoreAdapter
                                    holder.storeRv.layoutManager = LinearLayoutManager(holder.storeRv.context, LinearLayoutManager.VERTICAL, false)
                                }
                                1 -> {
                                    //실패
                                    Log.d("store checking", "fail")
                                }
                            }
                        }
                    }
                    else if (flag == "feed"){
                        viewModel!!.getAllStores(categoryId = holder.data!!.myCategoryId, nickname = viewModel!!.currentFeedNickname){
                                result ->
                            when(result){
                                0 -> {
                                    //success
                                    val mStoreAdapter = ListViewStoreAdapter(flag, parentView)
                                    mStoreAdapter.submitList(viewModel!!.myAllStoreList!!)
                                    mStoreAdapter.gustoViewModel = viewModel
                                    holder.storeRv.adapter = mStoreAdapter
                                    holder.storeRv.layoutManager = LinearLayoutManager(holder.storeRv.context, LinearLayoutManager.VERTICAL, false)
                                }
                                1 -> {
                                    //실패
                                    Log.d("store checking", "fail")
                                }
                            }
                        }
                    }
                    else{
                        viewModel!!.getMapStores(holder.data!!.myCategoryId, townName = viewModel!!.dong.value!!){
                                result ->
                            when(result){
                                0 -> {
                                    //성공
                                    val mStoreAdapter = ListViewStoreAdapter(flag, parentView)
                                    mStoreAdapter.submitList(viewModel!!.myMapStoreList!!)
                                    mStoreAdapter.gustoViewModel = viewModel
                                    holder.storeRv.adapter = mStoreAdapter
                                    holder.storeRv.layoutManager = LinearLayoutManager(holder.storeRv.context, LinearLayoutManager.VERTICAL, false)
                                }
                                1 -> {
                                    //실패
                                    Log.d("store checking", "fail")
                                }
                            }
                        }
                    }


                }
            }
            else {

            }
        }

        //개수에 따라 펼치기 활성 상태 변경
        if(holder.data?.pinCnt!! <= 0) {
            holder.ivUpDown.imageTintList = ColorStateList.valueOf(Color.parseColor("#ECECEC"))
        }

        // route인 경우 롱클릭 비활성화
        if(flag != "route"){
            holder.updownLayout.setOnLongClickListener {
                val categoryBottomSheetDialog = CategoryBottomSheetDialog(){
                    when(it){
                        0 -> {
                            //성공 -> 카테고리 목록 새로 불러오기
                            viewModel!!.changeEditFlag(viewModel!!.cateEditFlag.value!!)
                        }
                        1 -> {
                            //실패
                        }
                    }
                }
                categoryBottomSheetDialog.isAdd = false
                categoryBottomSheetDialog.categoryEdiBottomSheetData = CategoryDetail(id = holder.data!!.myCategoryId, categoryName = holder.data!!.categoryName, categoryDesc = "냠냠", categoryIcon = 1, isPublic = true )
                categoryBottomSheetDialog.viewModel = viewModel
                categoryBottomSheetDialog.show(mFragmentManager, categoryBottomSheetDialog.tag)
                true
            }
        }
    }

}