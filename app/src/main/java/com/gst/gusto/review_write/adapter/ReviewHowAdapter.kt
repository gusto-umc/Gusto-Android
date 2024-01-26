package com.gst.gusto.review_write.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R

data class HowItem(val name: String, val option: Int)

class ReviewHowAdapter(val gradeList : MutableList<Int>, val option : Int):
    RecyclerView.Adapter<ReviewHowAdapter.ReviewHowViewHolder>(){

    private val itemList = arrayListOf(HowItem("맛슐랭",0),HowItem("맵기",1)
        ,HowItem("분위기",3),HowItem("화장실",4),HowItem("주차장",5))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHowViewHolder {
        val view =
            if(option == 0) LayoutInflater.from(parent.context).inflate(R.layout.item_review_howabout, parent, false)
            else if(option == 1) LayoutInflater.from(parent.context).inflate(R.layout.item_review_howabout_feed, parent, false)
            else if(option == 2) LayoutInflater.from(parent.context).inflate(R.layout.item_review_howabout_review, parent, false)
            else LayoutInflater.from(parent.context).inflate(R.layout.item_review_howabout_review_edit, parent, false)
        return ReviewHowViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewHowViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.tv_name.text = currentItem.name

        val gradeViews = listOf(
            holder.tv_grade1,
            holder.tv_grade2,
            holder.tv_grade3,
            holder.tv_grade4,
            holder.tv_grade5
        )

        for(i in 0..4) {
            gradeViews[i].setOnClickListener {
                // Set color for tv_grade1
                setGradeColors(gradeViews, i, R.color.main_C)
                gradeList[currentItem.option] = i+1
            }
        }

    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ReviewHowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val tv_grade1 = itemView.findViewById<ImageView>(R.id.iv_grade1)
        val tv_grade2 = itemView.findViewById<ImageView>(R.id.iv_grade2)
        val tv_grade3 = itemView.findViewById<ImageView>(R.id.iv_grade3)
        val tv_grade4 = itemView.findViewById<ImageView>(R.id.iv_grade4)
        val tv_grade5 = itemView.findViewById<ImageView>(R.id.iv_grade5)
    }

    private fun setGradeColors(gradeViews: List<ImageView>, selectedGrade: Int, selectedColor: Int) {
        gradeViews.forEachIndexed { index, gradeView ->
            val color = if (index <= selectedGrade) {
                ContextCompat.getColor(gradeView.context, selectedColor)
            } else {
                Color.parseColor("#B7B7B7")
            }
            gradeView.setColorFilter(color)
        }
    }

}

