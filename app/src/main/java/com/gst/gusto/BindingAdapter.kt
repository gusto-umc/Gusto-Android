package com.gst.gusto.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.gst.gusto.R

object BindingAdapters {

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String?) {
        if (url.isNullOrEmpty()) {
            // 기본 이미지 설정
            imageView.setImageResource(R.drawable.gst_dummypic)
        } else {
            // Glide를 사용하여 이미지 로딩
            Glide.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.gst_dummypic) // 로딩 중 표시할 기본 이미지
                .into(imageView)
        }
    }
}
