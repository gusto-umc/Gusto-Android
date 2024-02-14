package com.gst.clock.Fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.Util.util.Companion.isPhotoPickerAvailable
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewAdd3Binding

class ReviewAdd3Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd3Binding
    private val handler = Handler()
    private val progressPoint = 200
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAdd3Binding.inflate(inflater, container, false)


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd3Fragment_to_reviewAdd4Fragment)
        }
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_reviewAdd3Fragment_to_reviewAdd4Fragment)
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            binding.btnSkip.visibility = View.GONE
            binding.btnNext.visibility = View.VISIBLE
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
    private fun setImageViewSize(imageView: CardView, length: Int) {
        val layoutParams = imageView.layoutParams
        layoutParams.width = length
        layoutParams.height = length
        imageView.layoutParams = layoutParams
    }

}