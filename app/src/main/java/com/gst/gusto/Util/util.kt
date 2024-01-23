package com.gst.gusto.Util

import android.os.Handler
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.ProgressBar

class util {

    companion object {
        //dp를 pixel(int) 값으로
        fun Int.dpToPx(scale : Float): Int {
            return (this * scale + 0.5f).toInt()
        }
        //dp를 pixel 값으로
        fun dpToPixels(dp: Float, metrics : DisplayMetrics): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics)
        }
        //프로그래스바 진행도 부드럽게 움직이기
        fun createUpdateProgressRunnable(progressBar: ProgressBar, progressPoint: Int,handler : Handler): Runnable {
            var delay = 4L

            return  when {
                progressBar.progress < progressPoint -> object : Runnable {
                    private var width = 8
                    override fun run() {
                        if (progressBar.progress < progressPoint) {
                            progressBar.progress+=width
                            handler.postDelayed(this, delay) // 딜레이 조절 (여기서는 0.5초로 설정)
                            delay+=4
                        }
                    }
                }
                progressBar.progress > progressPoint -> object : Runnable {
                    private var width = -8
                    override fun run() {
                        if (progressBar.progress > progressPoint) {
                            progressBar.progress+=width
                            handler.postDelayed(this, delay) // 딜레이 조절 (여기서는 0.5초로 설정)
                            delay+=4
                        }
                    }
                }
                else -> object : Runnable {
                    override fun run() {
                    }
                }
            }
        }


    }
}