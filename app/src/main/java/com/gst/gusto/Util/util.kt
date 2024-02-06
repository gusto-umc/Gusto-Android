package com.gst.gusto.Util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.os.ext.SdkExtensions
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.gst.gusto.ListView.ViewModel.MapViewModel
import com.gst.gusto.R
import kotlin.concurrent.thread

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

        /**
         * 작업자 : 버루(But 인터넷에 있는 코드)
         * 이 메서드는 LinearLayout을 View.GONE 상태에서 View.VISIBLE 상태로 바뀔 때 애니메이션을 추가 해 준다
         * @param isExpanded 펼칠 떄 true, 닫을 때 false
         * @param layoutExpand 해당 LinearLayout
         * @return true or false
         */
        fun toggleLayout(isExpanded: Boolean, layoutExpand: LinearLayout): Boolean {
            if (isExpanded &&layoutExpand.visibility == View.GONE) {
                ToggleAnimation.expand(layoutExpand)
            } else if(!isExpanded &&layoutExpand.visibility == View.VISIBLE){
                ToggleAnimation.collapse(layoutExpand)
            }
            return isExpanded
        }

        fun setPopupOne(context: Context, title : String, theView : View, desc : String){
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_one_button, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .create()

            mBuilder?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mBuilder?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            mBuilder.show()

            //팝업 사이즈 조절
            DisplayMetrics()
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val size = Point()
            val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            display.getSize(size)
            val screenWidth = size.x
            val popupWidth = (screenWidth * 0.8).toInt()
            mBuilder?.window?.setLayout(popupWidth, WindowManager.LayoutParams.WRAP_CONTENT)

            //팝업 타이틀 설정, 버튼 작용 시스템
            mDialogView.findViewById<TextView>(R.id.tv_dialog_one_text).text = title
            mDialogView.findViewById<ImageButton>(R.id.btn_dialog_one).setOnClickListener( {
                mBuilder.dismiss()
            })
            mDialogView.findViewById<TextView>(R.id.tv_dialog_one_desc).text  = desc

        }

        fun setPopupTwo(context: Context, title: String, theView : View, desc : String, flag : String, viewModel : MapViewModel){
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_two_button, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .create()

            mBuilder?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mBuilder?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            mBuilder.show()

            //팝업 사이즈 조절
            DisplayMetrics()
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val size = Point()
            val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            display.getSize(size)
            val screenWidth = size.x
            val popupWidth = (screenWidth * 0.8).toInt()
            mBuilder?.window?.setLayout(popupWidth, WindowManager.LayoutParams.WRAP_CONTENT)

            //flag별 동작 적용

            mDialogView.findViewById<TextView>(R.id.tv_dialog_two_text).text = title
            mDialogView.findViewById<TextView>(R.id.tv_dialog_two_desc).text = desc
            mDialogView.findViewById<TextView>(R.id.btn_dialog_two_no).text = "no"
            mDialogView.findViewById<TextView>(R.id.btn_dialog_two_yes).text = "yes"

            mDialogView.findViewById<TextView>(R.id.btn_dialog_two_no).setOnClickListener {
                mBuilder.dismiss()
            }

            mDialogView.findViewById<TextView>(R.id.btn_dialog_two_yes).setOnClickListener {
                mBuilder.dismiss()
            }
        }

        /**
         * 작업자 : 옌 (출처 다른 분)
         * 이 메서드는 fragment에서 사용가능하며, 포커스를 주고 키보드를 올려준다.
         */
        fun showSoftInputFragment(view:View, context: FragmentActivity?){
            // 포커스를 준다.
            view.requestFocus()

            thread {
                SystemClock.sleep(1)
                val inputMethodManager = context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(view, 0)
            }
        }

        /**
         * 작업자 : 옌 (출처 다른 분)
         * 이 메서드는 activity에서 사용가능하며, 키보드를 내려준다.
         */
        fun hideSoftInput(activity:AppCompatActivity){
            // 현재 포커스를 가지고 있는 View 있다면 키보드를 내린다.
            if(activity.window.currentFocus != null){
                val inputMethodManager = activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.window.currentFocus?.windowToken, 0);
            }

        }
    }
}