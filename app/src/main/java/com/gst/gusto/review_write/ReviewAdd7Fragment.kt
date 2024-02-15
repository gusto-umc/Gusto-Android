package com.gst.clock.Fragment

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.createUpdateProgressRunnable
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewAdd7Binding
import com.gst.gusto.list.adapter.LisAdapter

class ReviewAdd7Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd7Binding
    private val handler = Handler()
    private val progressPoint = 600
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var activity: MainActivity
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
            gustoViewModel.comment = binding.etContent.text.toString()
            gustoViewModel.createReview() {result ->
                when(result) {
                    1 -> {
                        activity.hideBottomNavigation(false)
                        findNavController().popBackStack(R.id.storeDetailFragment,false)
                    }
                }
            }

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
