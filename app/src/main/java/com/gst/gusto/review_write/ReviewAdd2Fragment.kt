package com.gst.clock.Fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.FragmentReviewAdd2Binding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReviewAdd2Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd2Binding
    lateinit var progressBar : ProgressBar
    private val handler = Handler()
    private val progressPoint = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd2Binding.inflate(inflater, container, false)
        val bundle = Bundle().apply {
            putInt("progress", progressPoint)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd2Fragment_to_reviewAdd1Fragment,bundle)
        }
        binding.btnNext.setOnClickListener {
            val year = if(binding.etYear.text.toString()=="") binding.etYear.hint.toString().toInt() else binding.etYear.text.toString().toInt()
            val month =  if(binding.etMonth.text.toString()=="") binding.etMonth.hint.toString().toInt() else binding.etMonth.text.toString().toInt()
            val day =  if(binding.etDay.text.toString()=="") binding.etDay.hint.toString().toInt() else binding.etDay.text.toString().toInt()
            if(year<2000 || month<1||month>12 || day<1 || day>31) {

            } else {
                Log.d("dayList","${year}-${month}-${day}")
                findNavController().navigate(R.id.action_reviewAdd2Fragment_to_reviewAdd3Fragment,bundle)
            }
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()

        // 오늘 날짜를 얻기
        val currentDate = calendar.time

        // 년, 월, 일을 분리
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val monthFormat = SimpleDateFormat("M", Locale.getDefault())
        val dayFormat = SimpleDateFormat("d", Locale.getDefault())

        val year = yearFormat.format(currentDate)
        val month = monthFormat.format(currentDate)
        val day = dayFormat.format(currentDate)

        binding.etYear.hint = year
        binding.etMonth.hint = month
        binding.etDay.hint = day

        binding.etYear.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                // 완료 버튼을 눌렀을 때 처리
                binding.etMonth.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }
        binding.etMonth.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                // 완료 버튼을 눌렀을 때 처리
                binding.etDay.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        progressBar = binding.pBar
        progressBar.progress = arguments?.getInt("progress", progressPoint)!!

        val updateProgressRunnable = util.createUpdateProgressRunnable(progressBar, progressPoint, handler)

        // 올릴 때 마다 부드럽게 움직이도록 시작
        handler.post(updateProgressRunnable)


    }

}