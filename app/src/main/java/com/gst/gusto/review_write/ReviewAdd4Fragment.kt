package com.gst.clock.Fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ext.SdkExtensions.getExtensionVersion
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.databinding.FragmentReviewAdd4Binding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReviewAdd4Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd4Binding
    lateinit var progressBar : ProgressBar
    private val menuList = ArrayList<EditText>()
    private val handler = Handler()
    private val progressPoint = 300

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd4Binding.inflate(inflater, container, false)

        val bundle = Bundle().apply {
            putInt("progress", progressPoint)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd4Fragment_to_reviewAdd3Fragment,bundle)
        }
        binding.btnNext.setOnClickListener {
            val textList = ArrayList<String>()

            for (editText in menuList) {
                val text = editText.text.toString()
                textList.add(text)
            }
            Log.d("menuList",textList.toString())
            findNavController().navigate(R.id.action_reviewAdd4Fragment_to_reviewAdd5Fragment,bundle)
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.pBar
        progressBar.progress = arguments?.getInt("progress", progressPoint)!!

        val updateProgressRunnable = util.createUpdateProgressRunnable(progressBar, progressPoint, handler)
        // 올릴 때 마다 부드럽게 움직이도록 시작
        handler.post(updateProgressRunnable)

        addMenu()
        binding.ivPlusMenu.setOnClickListener {
            addMenu()
        }
    }

    private fun addMenu() {
        val newText = EditText(requireContext())
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.food_menu_text_size))
        layoutParams.setMargins(0,  dpToPixels(8f,resources.displayMetrics).toInt(), 0, 0)
        newText.layoutParams = layoutParams

        newText.setPadding(dpToPixels(13f, resources.displayMetrics).toInt(), 0, 0, 0)
        newText.textSize = 14f // 14sp
        newText.setTextColor(resources.getColor(R.color.black))
        newText.typeface = resources.getFont(R.font.pretendard_medium)
        newText.setBackgroundResource(R.drawable.background_radius_10_stroke_1)
        newText.backgroundTintList = resources.getColorStateList(R.color.main_C)
        newText.hint = "예) 수타면 파수타"
        newText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))

        menuList.add(newText)
        binding.lyMenus.addView(newText)
    }
}
