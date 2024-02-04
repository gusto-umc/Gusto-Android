package com.gst.gusto.store_detail.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.StoreDetailReview
import com.gst.gusto.R
import com.gst.gusto.databinding.ItemStoreDetailReviewBinding
import java.time.LocalDate

class StoreDetailReviewAdapter () : ListAdapter<StoreDetailReview, StoreDetailReviewAdapter.ViewHolder>(
    DiffCallback) {



    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<StoreDetailReview>(){
            override fun areItemsTheSame(oldItem: StoreDetailReview, newItem: StoreDetailReview): Boolean {
                //아이템  id 가 같은지 확인
                return oldItem.reviewId == newItem.reviewId
            }

            override fun areContentsTheSame(oldItem: StoreDetailReview, newItem: StoreDetailReview): Boolean {
                //아이템 내용이 같은 지 확인
                return oldItem == newItem
            }

        }
    }


    inner class ViewHolder(private val binding : ItemStoreDetailReviewBinding) : RecyclerView.ViewHolder(binding.root){
        var data : StoreDetailReview? = null
        var photoArray : ArrayList<Int>? = null
        var heartFlag = false

        fun bind(review: StoreDetailReview){
            data = review
            //유저 사진
            //binding.ivStoreDetailProfileImg.setImageResource()
            //유저 이름
            binding.tvStoreDetailUsername.text = review.nickname
            //리뷰 코멘트
            binding.tvStoreDetailReview.text = review.comment
            //하트 개수
            binding.tvStoreDetailHeartCount.text = review.liked.toString()
            //하트 여부 -> 아직 데이터 없음
            //heartFlag = review.heart
            //방문일자
            val reviewDate = LocalDate.parse(review.date)
            binding.tvStoreReviewDate.text = "${reviewDate.year}. ${reviewDate.monthValue}. ${reviewDate.dayOfMonth}"
            //리뷰 사진들 -> 아직 없음.

        }
        val layoutItem = binding.layoutReviewItemAll
        val ivHeart = binding.ivStoreDetailHeartOff
        val tvCount = binding.tvStoreDetailHeartCount
        val rvReviewPhoto = binding.rvStoreDetailReviewPhoto
        val ivSwipe = binding.ivStoreDetailReviewSwipe

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemStoreDetailReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position))
        fun changeHeart(onFlag : Boolean){
            Log.d("heartFlagCheck", onFlag.toString())
            if(onFlag){
                //하트여부 변경 후(하트x) notify + 서버 연결
                //
                //notifyDataSetChanged()
                holder.ivHeart.setImageResource(R.drawable.heart_img)
                holder.ivHeart.imageTintList = null
                holder.heartFlag = false
                holder.tvCount.text = "${holder.tvCount.text.toString().toInt() - 1}"
            }
            else{
                //하트여부 변경 후(하트x) notify + 서버 연결
                //
                //notifyDataSetChanged()
                holder.ivHeart.setImageResource(R.drawable.heart_img)
                holder.ivHeart.imageTintList = ColorStateList.valueOf(Color.parseColor("#F27781"))
                holder.heartFlag = true
                holder.tvCount.text = "${holder.tvCount.text.toString().toInt() + 1}"
            }
        }

        //하트 클릭 시 -> flag 확인 후 처리
        holder.ivHeart.setOnClickListener {
            changeHeart(holder.heartFlag)
        }

        //레이아웃 클릭 시 상세 리뷰 화면으로 이동
        holder.layoutItem.setOnClickListener {
            itemClickListener.onClick(it, holder.data!!)
        }

        //photoRv 연결하기
        if(!holder.data?.photoArray.isNullOrEmpty()){
            Log.d("reviewPhoto", "exist")
            holder.rvReviewPhoto.visibility = View.VISIBLE
            holder.ivSwipe.visibility = View.VISIBLE
            val mReviewPhotoAdapter = StoreDetailReviewPhotoAdapter(holder.data!!.photoArray!!)
            holder.rvReviewPhoto.adapter = mReviewPhotoAdapter
        }
        else{
            holder.rvReviewPhoto.visibility = View.GONE
            holder.ivSwipe.visibility = View.GONE
        }

    }

    interface OnItemClickListener {
        fun onClick(v: View, dataSet: StoreDetailReview)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

}