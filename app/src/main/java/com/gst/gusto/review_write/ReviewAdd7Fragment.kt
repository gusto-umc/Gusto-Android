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
import androidx.navigation.fragment.findNavController
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.createUpdateProgressRunnable
import com.gst.gusto.databinding.FragmentReviewAdd7Binding

class ReviewAdd7Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd7Binding
    lateinit var progressBar : ProgressBar
    private val handler = Handler()
    private val progressPoint = 600
    lateinit var activity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd7Binding.inflate(inflater, container, false)

        activity = requireActivity() as MainActivity

        val bundle = Bundle().apply {
            putInt("progress", progressPoint)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd7Fragment_to_reviewAdd6Fragment,bundle)
        }
        binding.btnBack2.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd7Fragment_to_reviewAdd6Fragment,bundle)
        }
        binding.btnEnd.setOnClickListener {
            activity.hideBottomNavigation(false)
            findNavController().navigate(R.id.action_reviewAdd7Fragment_to_storeDetailFragment)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.pBar
        progressBar.progress = arguments?.getInt("progress", progressPoint)!!
        val updateProgressRunnable = createUpdateProgressRunnable(progressBar, progressPoint,handler)
        // 올릴 때 마다 부드럽게 움직이도록 시작
        handler.post(updateProgressRunnable)

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
        binding

    }

}
