package com.gst.gusto.review.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.databinding.ItemReviewListBinding

class ListReviewViewHolder(
    private val binding: ItemReviewListBinding,
    private val itemClickListener: (ListReviewData) -> Unit,
    var testcount: Int = 0
) : RecyclerView.ViewHolder(binding.root) {

    val datetextview = binding.dateTextView
    val nametextview = binding.nameTextView
    val visittextview = binding.visitTextView
    val imageview1 = binding.imageView1
    val imageview2 = binding.imageView2
    val imageview3 = binding.imageView3
    val linearLayout = binding.linearLayout

    fun bind(listReviewList: ListReviewData){
        datetextview.text = listReviewList.date
        nametextview.text = listReviewList.name
        visittextview.text = "${listReviewList.visit}번 방문"

        setImageView(listReviewList.images,itemView.context)

        linearLayout.setOnClickListener {
            itemClickListener.invoke(listReviewList)
        }
    }

    fun setImageView(images: ArrayList<String>, context: Context){

        when(images.size){
            0-> {
                imageview1.visibility = View.VISIBLE
                setImage(imageview1, null, context)
            }
            1 -> {
                imageview1.visibility = View.VISIBLE
                setImage(imageview1, images[0], context)

            }
            2 -> {
                imageview1.visibility = View.VISIBLE
                imageview2.visibility = View.VISIBLE
                setImage(imageview1, images[0], context)
                setImage(imageview2, images[1], context)
            }
            else -> {
                imageview1.visibility = View.VISIBLE
                imageview2.visibility = View.VISIBLE
                imageview3.visibility = View.VISIBLE
                setImage(imageview1, images[0], context)
                setImage(imageview2, images[1], context)
                setImage(imageview2, images[2], context)
            }
        }
    }
}