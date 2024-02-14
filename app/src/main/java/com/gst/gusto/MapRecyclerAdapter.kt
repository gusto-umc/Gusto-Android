package com.gst.gusto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MapRecyclerAdapter(val list : List<Int>): RecyclerView.Adapter<MapRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder에 필요한 코드 작성
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // ViewHolder 생성 및 초기화
        val view = LayoutInflater.from(parent.context).inflate(R.layout.map_recycler_view_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        // 항목 수 반환
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ViewHolder에 데이터 바인딩
    }
}
