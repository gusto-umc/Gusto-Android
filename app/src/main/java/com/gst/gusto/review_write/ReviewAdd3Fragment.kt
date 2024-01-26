package com.gst.clock.Fragment

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
import android.widget.ImageView
import android.widget.ProgressBar
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
import com.gst.gusto.Util.util.Companion.isPhotoPickerAvailable
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.databinding.FragmentReviewAdd3Binding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReviewAdd3Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd3Binding
    lateinit var progressBar : ProgressBar
    private val handler = Handler()
    private val progressPoint = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd3Binding.inflate(inflater, container, false)

        val bundle = Bundle().apply {
            putInt("progress", progressPoint)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd3Fragment_to_reviewAdd2Fragment,bundle)
        }
        binding.btnBack2.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd3Fragment_to_reviewAdd2Fragment,bundle)
        }
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd3Fragment_to_reviewAdd4Fragment,bundle)
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.pBar
        progressBar.progress = arguments?.getInt("progress", progressPoint)!!


        val updateProgressRunnable = util.createUpdateProgressRunnable(progressBar, progressPoint, handler)

        val imageViews = listOf(
            binding.ivImgae1,
            binding.ivImgae2,
            binding.ivImgae3,
            binding.ivImgae4
        )
        val imageCards = listOf(
            binding.cvImgae1,
            binding.cvImgae2,
            binding.cvImgae3,
            binding.cvImgae4
        )

        // 올릴 때 마다 부드럽게 움직이도록 시작
        handler.post(updateProgressRunnable)

        var imagesOn = false

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(4)) { uri ->
            // Callback is invoked after th user selects a media item or closes the photo picker.
            if (uri != null) {
                if(!imagesOn) {
                    imagesOn= true
                    binding.lyImgaes.visibility = View.VISIBLE
                    binding.ivImage.visibility = View.GONE

                    val Images = binding.lyImgaes

                    binding.lyImgaes.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            // 뷰의 크기가 측정되면 호출되는 부분
                            // 가로 너비와 세로 너비 얻기
                            val width: Int = Images.width
                            val height: Int = Images.height
                            val marginInPixels = dpToPixels(10f,resources.displayMetrics)

                            // 리스너를 한 번 호출한 후 제거
                            Images.viewTreeObserver.removeOnPreDrawListener(this)

                            val length = if(width > height)  (height/2 - marginInPixels).toInt() else (width/2 - marginInPixels).toInt()

                            for(i in 0..3) {
                                setImageViewSize(imageCards[i], length)
                            }

                            return true
                        }
                    })
                    binding.tvUpload1.text = "업로드 완료!"
                    binding.tvUpload2.text = "이제 리뷰를 작성하러 가볼까요?"
                    binding.btnBack2.visibility = View.GONE
                    binding.btnNext.text = "리뷰 작성하러 가기"
                }

                for (j in 0 .. uri.size-1) {
                    setImage(imageViews[j],uri[j].toString(),requireContext())
                }
                for (j in uri.size .. 3) {
                    setImage(imageViews[j],"",requireContext())
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.ivImage.setOnClickListener {
            if(isPhotoPickerAvailable()) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
        for(i in 0..3) {
            imageCards[i].setOnClickListener {
                if(isPhotoPickerAvailable()) {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }


    }

    // 사진 불러오기 위한 SDK 검수

    // 이미지 적용



    // 이미지 크기 조절 함수
    private fun setImageViewSize(imageView: CardView, length: Int) {
        val layoutParams = imageView.layoutParams
        layoutParams.width = length
        layoutParams.height = length
        imageView.layoutParams = layoutParams
    }

}