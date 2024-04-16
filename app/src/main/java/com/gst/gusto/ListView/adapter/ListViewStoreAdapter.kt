package com.gst.gusto.ListView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseStoreListItem
import com.gst.gusto.databinding.CardWxampleBinding

class ListViewStoreAdapter(private var flag : String, private val parentView : View) : ListAdapter<ResponseStoreListItem, ListViewStoreAdapter.ViewHolder>(DiffCallback) {

    var gustoViewModel : GustoViewModel? = null
    var mContext : Context? = null
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
            if(!store.reviewImg.isNullOrEmpty()){
                util.setImage(binding.ivItemStoreImg, store.reviewImg, mContext!!)
            }
            else{
                binding.ivItemStoreImg.setImageResource(R.drawable.gst_dummypic)
            }
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
            holder.tvCountCategory.text = "${holder.data!!.reviewCnt}번 방문했어요"

            holder.cvStore.setOnClickListener {
                //루트 페이지로 이동
                val mainActivity = gustoViewModel?.mainActivity
                if (mainActivity != null) {
                    //mainActivity?.supportFragmentManager?.beginTransaction()?.remove(parentView.findFragment())?.commit()
                    gustoViewModel!!.routeStorTmpData = holder.data
                    mainActivity.getCon().popBackStack()
                }
            }
        }
        else if(flag == "my" || flag == "feed"){
            if(flag == "my"){
                holder.tvCountCategory.text = "${holder.data!!.reviewCnt}번 방문했어요"
            }
            else{
                holder.tvCountCategory.text = ""
            }
            holder.cvStore.setOnClickListener {
                //store detail로 이동
                gustoViewModel!!.selectedDetailStoreId = holder.data!!.storeId
                if(flag == "my"){
                    Navigation.findNavController(parentView).navigate(R.id.action_myFragment_to_storeDetailFragment)
                }
                else{
                    Navigation.findNavController(parentView).navigate(R.id.action_fragment_other_to_storeDetailFragment)
                }
            }
        }


        /**
         * 체크박스 클릭리스너
         */
        holder.cbEdit.setOnCheckedChangeListener { buttonView, isChecked ->

        }

    }
}