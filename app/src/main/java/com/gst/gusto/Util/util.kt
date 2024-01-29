package com.gst.gusto.Util

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.ext.SdkExtensions
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.gst.gusto.R

class util {

    companion object {
        /**
         * 작업자 : 버루
         * 이 메서드는 dp를 pixel 값을 변환 해준다
         * 예시 ) dpToPixels(5f,resources.displayMetrics) => 5dp
         * @param dp 원하는 dp 값
         * @param metrics 해당 metrics
         * @return 원하는 dp를 pixel 값으로 변환 후 반환
         */
        fun dpToPixels(dp: Float, metrics : DisplayMetrics): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics)
        }
        //

        /**
         * 작업자 : 버루
         * 이 메서드는 프로그래스바 진행도 부드럽게 움직인다
         * @param progressBar 프로그래스 바
         * @param progressPoint 프로그래스 바 진행도 (최대 600, 100 단위)
         * @param handler 해당 handler
         * @return null
         */
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


        /**
         * 작업자 : 버루
         * 이 메서드는 url에 있는 이미지를 imageView에 적용
         * @param imageView 이미지 뷰
         * @param url URL String 값
         * @param context 해당 CONTEXT
         * @return null
         */
        fun setImage(imageView: ImageView, url : String, context: Context) {
            Glide.with(context).load(url).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(imageView)
        }


        /**
         * 작업자 : 버루
         * 이 메서드는 사진 선택창 불러오기 위한 휴대폰 버전 체크
         * @return true or false
         */
        fun isPhotoPickerAvailable(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                true
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
            } else {
                false
            }
        }


    }
}