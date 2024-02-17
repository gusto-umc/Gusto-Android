package com.gst.clock.Fragment

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ext.SdkExtensions.getExtensionVersion
import android.text.Editable
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.createUpdateProgressRunnable
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewAdd5Binding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReviewAdd5Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd5Binding
    private val menuList = ArrayList<EditText>()
    private val handler = Handler()
    private val progressPoint = 400
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var  chipGroup: ChipGroup
    private val hashTag = listOf<String>("#따뜻함","#여기서는 화장실 금지","#쾌적","#귀여워","#깨끗함","#인스타","#힙함","#나름 괜찮아","#넓음","#분위기","#가성비")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd5Binding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        chipGroup = binding.chipGroup

        binding.btnNext.setOnClickListener {
            var tmpString = ""

            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    tmpString += (","+(i+1))
                }
            }
            if(tmpString !="") gustoViewModel.hashTagId = tmpString.substring(1)
            findNavController().navigate(R.id.action_reviewAdd5Fragment_to_reviewAdd6Fragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for(i in 0.. hashTag.size-1) {
            addChip(hashTag[i])
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
    private fun addChip(text:String) {
        val chip = Chip(requireContext())

        chip.isClickable = true
        chip.isCheckable = true

        chip.text  = text
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_select_color)
        chip.chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.main_C)
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_select_text_color))
        chip.textSize = 15f
        chip.typeface = Typeface.createFromAsset(requireActivity().assets, "font/pretendard_medium.otf")
        chip.chipStrokeWidth = dpToPixels(1f,resources.displayMetrics)
        chip.chipCornerRadius = dpToPixels(41f,resources.displayMetrics)

        chipGroup.addView(chip)
    }

}
