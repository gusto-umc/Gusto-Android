package com.gst.gusto.review_write.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.gst.gusto.R

data class HowItem(val name: String, val option: Int, val chip: Chip?)

class ReviewHowAdapter(val itemList: ArrayList<HowItem>, val gradeList : MutableList<Int>):
    RecyclerView.Adapter<ReviewHowAdapter.ReviewHowViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_howabout, parent, false)
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

        if(currentItem.option==0) holder.btn_exit.visibility =View.GONE
        if(currentItem.chip != null) {
            holder.btn_exit.setOnClickListener {
                itemList.removeAt(position)
                currentItem.chip.isChecked = false
                notifyItemRemoved(position)
            }
        }

    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ReviewHowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val tv_grade1 = itemView.findViewById<ImageView>(R.id.tv_grade1)
        val tv_grade2 = itemView.findViewById<ImageView>(R.id.tv_grade2)
        val tv_grade3 = itemView.findViewById<ImageView>(R.id.tv_grade3)
        val tv_grade4 = itemView.findViewById<ImageView>(R.id.tv_grade4)
        val tv_grade5 = itemView.findViewById<ImageView>(R.id.tv_grade5)
        val btn_exit = itemView.findViewById<ImageView>(R.id.btn_exit)
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

