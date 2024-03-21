package com.gst.clock.Fragment

import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.util.util.Companion.dpToPixels
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewAdd4Binding

class ReviewAdd4Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd4Binding
    private val menuList = ArrayList<EditText>()
    private val handler = Handler()
    private val progressPoint = 300
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd4Binding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnNext.setOnClickListener {
            val textList = ArrayList<String>()
            var tmpString = ""
            for (editText in menuList) {
                if(editText.text.toString() !="") {
                    val text = editText.text.toString()
                    textList.add(text)
                    tmpString += (","+editText.text.toString())
                }
            }
            if(tmpString != "") gustoViewModel.menuName = tmpString.substring(1)
            Log.d("menuList",textList.toString())
            findNavController().navigate(R.id.action_reviewAdd4Fragment_to_reviewAdd5Fragment)
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMenu()

        binding.ivPlusMenu.setOnClickListener {
            addMenu()
        }
    }
    override fun onPause() {
        super.onPause()
        gustoViewModel.progress = progressPoint
    }

    override fun onResume() {
        super.onResume()
        binding.pBar.progress = gustoViewModel.progress
        val updateProgressRunnable = util.createUpdateProgressRunnable(binding.pBar, progressPoint, handler)
        handler.post(updateProgressRunnable)
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
