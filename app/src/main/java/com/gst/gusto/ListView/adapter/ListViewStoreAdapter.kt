package com.gst.gusto.ListView.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.ListView.Model.StoreSearch
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseStoreListItem
import com.gst.gusto.databinding.CardWxampleBinding
import com.gst.gusto.databinding.ItemStoreCardBinding

class ListViewStoreAdapter(private var flag : String, private val parentView : View) : ListAdapter<ResponseStoreListItem, ListViewStoreAdapter.ViewHolder>(DiffCallback) {

    var gustoViewModel : GustoViewModel? = null
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ResponseStoreListItem>(){
            override fun areItemsTheSame(oldItem: ResponseStoreListItem, newItem: ResponseStoreListItem): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.storeId == newItem.storeId
            }

            override fun areContentsTheSame(oldItem: ResponseStoreListItem, newItem: ResponseStoreListItem): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val binding : CardWxampleBinding) : RecyclerView.ViewHolder(binding.root){
        var data : ResponseStoreListItem? = null
        fun bind(store: ResponseStoreListItem){
            data = store
            binding.ivItemStoreImg.setImageResource(R.drawable.sample_store_img)
            binding.tvItemStoreTitle.text = store.storeName
            binding.tvItemStoreLocation.text = store.address
        }
        val cbEdit = binding.cbItemStoreEdit
        val tvCountCategory = binding.tvItemStoreCount
        val cvStore = binding.cvItemStoreEdit
        val tvStoreName = binding.tvItemStoreTitle
        val tvStoreLocation = binding.tvItemStoreLocation
        var ivPhoto = binding.ivItemStoreImg
        var layoutData = binding.layoutCardData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(CardWxampleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        if(flag == "show"){
            holder.cbEdit.visibility = View.GONE
            holder.tvCountCategory.text = "${holder.data?.reviewCnt}번 방문했어요"

            holder.cvStore.setOnClickListener {
                //뷰모델에 storeId 저장 -> detail 로 이동인 경우
                gustoViewModel!!.selectedDetailStoreId = holder.data?.storeId!!.toInt()
                Navigation.findNavController(parentView).navigate(R.id.action_mapListViewFragment_to_storeDetailFragment)
            }
        }
        else if(flag == "edit"){

            holder.cbEdit.visibility = View.VISIBLE
            holder.cbEdit.isChecked = false
            holder.tvCountCategory.text = "${holder.data!!.reviewCnt}번 방문했어요"
        }
        else if(flag == "route"){
            holder.tvCountCategory.text = "${holder.data!!.reviewCnt}"

            holder.cvStore.setOnClickListener {
                //루트 페이지로 이동
            }
        }


        /**
         * 체크박스 클릭리스너
         */
        holder.cbEdit.setOnCheckedChangeListener { buttonView, isChecked ->

        }

    }

    interface OnItemClickListener {
        fun onClick(v: View, dataSet: ResponseStoreListItem)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
}