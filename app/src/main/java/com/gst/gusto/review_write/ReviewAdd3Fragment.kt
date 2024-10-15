package com.gst.clock.Fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.util.util.Companion.convertContentToFile
import com.gst.gusto.util.util.Companion.dpToPixels
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewAdd3Binding
import java.io.File

class ReviewAdd3Fragment : Fragment() {

    lateinit var binding: FragmentReviewAdd3Binding
    private val handler = Handler()
    private val progressPoint = 200
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private val imageList: MutableList<File?> = MutableList(4) { null }

    private lateinit var activityResult: ActivityResultLauncher<Intent>
    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1001
    }


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
            var count = 0
            gustoViewModel.imageFiles.clear()
            for(data in imageList) {
                if(data !=null) {
                    gustoViewModel.imageFiles.add(data)
                    count++
                }
            }
            if(count != 0)
                findNavController().navigate(R.id.action_reviewAdd3Fragment_to_reviewAdd4Fragment)
            else
                Toast.makeText(context,"사진은 최소 1장이 필요합니다", Toast.LENGTH_SHORT ).show()
        }

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

        activityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageList.clear()

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

                val data = result.data
                data?.let {
                    if (it.clipData != null) {
                        var count = it.clipData!!.itemCount
                        if(count>4) {
                            Toast.makeText(requireContext(),"사진은 4장까지만 가능합니다",Toast.LENGTH_SHORT).show()
                            count = 4
                        }
                        for (index in 0 until count ) {
                            val imageUri = it.clipData!!.getItemAt(index).uri
                            imageList.add(convertContentToFile(requireContext(),imageUri))
                            setImage(imageViews[index],imageList.get(index).toString(),requireContext())
                        }
                        for(index in count until 4) {
                            setImage(imageViews[index],"",requireContext())
                        }
                    } else {
                        val imageUri = it.data
                        imageList.add(imageUri?.let { it1 ->
                            convertContentToFile(requireContext(),
                                it1
                            )
                        })
                        setImage(imageViews[0],imageList.get(0).toString(),requireContext())
                        for(index in 1 until 4) {
                            setImage(imageViews[index],"",requireContext())
                        }
                    }

                }
            }
        }
        binding.ivImage.setOnClickListener {
            binding.btnSkip.visibility = View.GONE
            binding.btnNext.visibility = View.VISIBLE

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            activityResult.launch(intent)
        }
        for(i in 0..3) {
            imageCards[i].setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                activityResult.launch(intent)
            }
        }

        return binding.root
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