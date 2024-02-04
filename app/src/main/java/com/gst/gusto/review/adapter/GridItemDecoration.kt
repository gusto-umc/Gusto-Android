package com.gst.gusto.review.adapter

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

// Kotlin
class GridItemDecoration(private val size: Int, private val color: Int) : RecyclerView.ItemDecoration() {
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = size.toFloat()
        color = this@GridItemDecoration.color
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = child.right + params.rightMargin
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }
}