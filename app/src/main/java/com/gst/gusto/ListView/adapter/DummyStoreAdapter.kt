package com.gst.gusto.ListView.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R

// 더미 데이터 어댑터
class DummyStoreAdapter(private val storeList: List<StoreData>) :
    RecyclerView.Adapter<DummyStoreAdapter.StoreViewHolder>() {

    // 뷰 홀더 클래스 정의
    class StoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val storeTitle: TextView = view.findViewById(R.id.tv_item_store_title)
        val storeCategory: TextView = view.findViewById(R.id.tv_item_store_category)
        val storeLocation: TextView = view.findViewById(R.id.tv_item_store_location)
        val storeImage1: ImageView = view.findViewById(R.id.iv_item_store_img1)
        val storeImage2: ImageView = view.findViewById(R.id.iv_item_store_img2)
        val storeImage3: ImageView = view.findViewById(R.id.iv_item_store_img3)
    }

    // 아이템 레이아웃을 인플레이트하여 뷰 홀더를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(view)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = storeList[position]
        holder.storeTitle.text = store.title
        holder.storeCategory.text = store.category
        holder.storeLocation.text = store.location

        // 이미지 설정 (더미 데이터로 이미지 리소스를 설정)
        holder.storeImage1.setImageResource(store.imageRes1)
        holder.storeImage2.setImageResource(store.imageRes2)
        holder.storeImage3.setImageResource(store.imageRes3)
    }

    // 리스트의 아이템 개수 반환
    override fun getItemCount() = storeList.size
}

// 더미 데이터를 위한 데이터 클래스
data class StoreData(
    val title: String,
    val category: String,
    val location: String,
    val imageRes1: Int,
    val imageRes2: Int,
    val imageRes3: Int
)
