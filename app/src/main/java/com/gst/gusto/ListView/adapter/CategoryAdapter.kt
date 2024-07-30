package com.gst.gusto.ListView.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.CategoryDetail
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseMapCategory
import com.gst.gusto.databinding.ItemCategoryBinding
import com.gst.gusto.util.util


class CategoryAdapter(private val view: View, val flag : String, private val fragmentManager : FragmentManager) : ListAdapter<ResponseMapCategory, CategoryAdapter.ViewHolder>(diffUtil){
    var viewModel : GustoViewModel? = null
    var mContext : Context? = null
    private val mFragmentManager = fragmentManager
    inner class ViewHolder(private val binding : ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseMapCategory? = null
        fun bind(item : ResponseMapCategory){
            binding.apply {
                tvItemCategoryTitle.text = item.categoryName
                ivItemCategoryIcon.setImageResource(viewModel!!.findIconResource(item!!.categoryIcon))
                tvItemCategoryCount.text = "${item.pinCnt}개"
            }
            data = item
        }
        val categoryLayout = binding.layoutItemCategory
        val popup = binding.btnItemCategoryPopup
        val cv = binding.cvCategoryMenu


    }

    companion object {
        // diffUtil: currentList에 있는 각 아이템들을 비교하여 최신 상태를 유지하도록 한다.
        val diffUtil = object : DiffUtil.ItemCallback<ResponseMapCategory>() {
            override fun areItemsTheSame(oldItem: ResponseMapCategory, newItem: ResponseMapCategory): Boolean {
                return oldItem.myCategoryId == newItem.myCategoryId
            }

            override fun areContentsTheSame(oldItem: ResponseMapCategory, newItem: ResponseMapCategory): Boolean {
                return oldItem == newItem
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])

        if(flag == "map"){
            holder.popup.visibility = View.VISIBLE
            holder.categoryLayout.setOnClickListener {
                viewModel!!.selectedCategoryInfo = holder.data
                val bundle1 = Bundle()
                bundle1.putString("sign", "map")
                Navigation.findNavController(view).navigate(R.id.action_categoryFragment_to_storeFragment, bundle1)
            }
            holder.popup.setOnClickListener {
                val popup = PopupMenu(mContext, it)
                popup.menuInflater.inflate(R.menu.category_menu, popup.menu)
                popup.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.category_edit -> {
                            //수정 페이지 등장
                            var isPublic : Boolean = holder.data!!.publishCategory == "PUBLIC"
                            var categoryData = CategoryDetail(id = holder.data!!.myCategoryId, categoryName = holder.data!!.categoryName, categoryDesc = holder.data!!.myCategoryScript, categoryIcon = holder.data!!.categoryIcon, isPublic = isPublic)
                            val categoryBottomSheetDialog = CategoryBottomSheetDialog(data = categoryData){
                                when(it){
                                    0 -> {
                                        Log.d("bottomsheet", "저장 click")
                                        //카테고리 data update
                                    }
                                }
                            }
                            categoryBottomSheetDialog.isAdd = false
                            Log.d("data check", categoryBottomSheetDialog.categoryEdiBottomSheetData.toString())
                            categoryBottomSheetDialog.show(mFragmentManager, categoryBottomSheetDialog.tag)
                        }
                        R.id.category_delete -> {
                            //삭제 다이얼로그 등장
                            util.setPopupTwo(mContext!!, "카테고리 삭제 시, 카테고리에\n포함된 맛집들도 함께 삭제됩니다", desc = "*카테고리 삭제 시 데이터복원은 불가능합니다", 1){
                                result ->
                                when(result){
                                    0 -> {
                                        //yes : remove
                                        //카테고리 삭제 진행
                                        viewModel!!.deleteCateogories(mutableListOf(holder.data!!.myCategoryId)){
                                                result ->
                                            when(result){
                                                0 -> {
                                                    //success
//                                                    getMapCategories()
                                                    //카테고리 data update
                                                }
                                                1 -> {
                                                    //fail
                                                    Toast.makeText(mContext, "삭제 실패", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }

                                    }
                                    1 -> {
                                        //no

                                    }
                                }
                            }
                        }
                    }
                    true
                }
                popup.show()
            }
        }
        else if(flag == "my"){
            holder.popup.visibility = View.INVISIBLE
            holder.categoryLayout.setOnClickListener {
                viewModel!!.selectedCategoryInfo = holder.data
                val bundle2 = Bundle()
                bundle2.putString("sign", "my")
                Navigation.findNavController(view).navigate(R.id.action_myFragment_to_storeFragment, bundle2)
            }
        }
        else if(flag == "feed"){
            holder.popup.visibility = View.INVISIBLE
            holder.categoryLayout.setOnClickListener {
                viewModel!!.selectedCategoryInfo = holder.data
                val bundle3 = Bundle()
                bundle3.putString("sign", "feed")
                Navigation.findNavController(view).navigate(R.id.action_fragment_other_to_StoreFragment, bundle3)
            }
        }
        else if(flag == "search"){
            holder.popup.visibility = View.INVISIBLE
            holder.categoryLayout.setOnClickListener {
                viewModel!!.selectedCategoryInfo = holder.data
                val bundle4 = Bundle()
                bundle4.putString("sign", "search")
                Navigation.findNavController(view).navigate(R.id.action_routeSearchFragment_to_storeFragment, bundle4)
            }
        }


        }
    }


