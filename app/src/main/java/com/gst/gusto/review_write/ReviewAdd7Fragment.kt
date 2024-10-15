package com.gst.clock.Fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewAdd7Binding

class ReviewAdd7Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd7Binding
    private val handler = Handler()
    private val progressPoint = 600
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var activity: MainActivity

    var public = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd7Binding.inflate(inflater, container, false)

        activity = requireActivity() as MainActivity

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnEnd.setOnClickListener {
            binding.btnEnd.isClickable = false
            gustoViewModel.comment = binding.etContent.text.toString()
            gustoViewModel.publishCheck = public
            gustoViewModel.createReview() {result ->
                when(result) {
                    1 -> {
                        activity.hideBottomNavigation(false)
                        if(gustoViewModel.reviewReturnPos == 0)
                            findNavController().popBackStack(R.id.storeDetailFragment,false)
                        else
                            findNavController().popBackStack(R.id.review_fragment,false)
                    }
                    else -> binding.btnEnd.isClickable = true
                }
            }

        }
        binding.btnPublic.setOnClickListener {
            binding.btnPublic.text = "• 전체공개"
            binding.btnPrivate.text = "나만보기"
            binding.btnPublic.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.main_C))
            binding.btnPrivate.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.gray_7))
            public = true
        }
        binding.btnPrivate.setOnClickListener {
            binding.btnPublic.text = "전체공개"
            binding.btnPrivate.text = "• 나만보기"
            binding.btnPublic.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.gray_7))
            binding.btnPrivate.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.main_C))
            public = false
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이벤트 발생 전에 수행할 작업
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 수행할 작업
                binding.tvEtNum.text = "${binding.etContent.text.length}/200"
            }

            override fun afterTextChanged(s: Editable?) {
                // 이벤트 발생 후에 수행할 작업
            }
        })
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
}
