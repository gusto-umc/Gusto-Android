package com.gst.gusto.util

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE

object ScrollUtil {

    fun RecyclerView.addGridOnScrollEndListener(
        threshold: Int = 1,
        callback: () -> Unit,
    ) {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // 끝에 닿은 경우
                if (newState == SCROLL_STATE_IDLE && recyclerView.hasLessItemThan(threshold)) {
                    callback.invoke()
                }
            }

            private fun RecyclerView.hasLessItemThan(threshold: Int): Boolean {
                // 목록이 갱신되는 중인 경우에는 false 반환
                if (isLayoutRequested) {
                    return false
                }
                (layoutManager as? GridLayoutManager)?.let {
                    val lastVisibleItem = it.findLastVisibleItemPosition()

                    return lastVisibleItem >= it.itemCount - threshold
                }
                return false
            }
        })
    }

    fun RecyclerView.addLinearOnScrollEndListener(
        threshold: Int = 2,
        callback: () -> Unit,
    ) {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // 끝에 닿은 경우
                if (newState == SCROLL_STATE_IDLE && recyclerView.hasLessItemThan(threshold)) {
                    callback.invoke()
                }
            }

            private fun RecyclerView.hasLessItemThan(threshold: Int): Boolean {
                // 목록이 갱신되는 중인 경우에는 false 반환
                if (isLayoutRequested) {
                    return false
                }
                (layoutManager as? LinearLayoutManager)?.let {
                    val lastVisibleItem = it.findLastVisibleItemPosition()

                    return lastVisibleItem >= it.itemCount - threshold
                }
                return false
            }
        })
    }
}