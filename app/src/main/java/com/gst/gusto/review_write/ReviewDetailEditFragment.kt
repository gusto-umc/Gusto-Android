package com.gst.clock.Fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewDetailEditBinding
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter
import com.gst.gusto.util.util.Companion.setImage
import java.time.LocalDate

class ReviewDetailEditFragment : Fragment() {

    lateinit var binding: FragmentReviewDetailEditBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewDetailEditBinding.inflate(inflater, container, false)

        val receivedBundle = arguments

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 적용
        //reviewId 가져오기
        val editReview = gustoViewModel.myReview
        val reviewDate = LocalDate.parse(gustoViewModel.myReview!!.visitedAt)
        binding.tvDay.text = "${reviewDate.year}. ${reviewDate.monthValue}. ${reviewDate.dayOfMonth}"
        binding.tvReviewStoreNameEdit.text = editReview!!.storeName


        //ratingbar 적용
        binding.ratingbarTasteEdit.rating = editReview!!.taste.toFloat()
        /*
        binding.ratingbarSpicenessEdit.rating = if(editReview!!.spiciness == null){ 3.0F } else{ editReview!!.spiciness!!.toFloat() }
        binding.ratingbarMoodEdit.rating = if(editReview!!.mood == null){ 3.0F } else{editReview!!.mood!!.toFloat()}
        binding.ratingbarToiletEdit.rating = if(editReview!!.toilet == null ){3.0F}else{editReview!!.toilet!!.toFloat()}
        binding.ratingbarParkingEdit.rating = if(editReview!!.parking == null){3.0F} else{editReview!!.parking!!.toFloat()}*/

        //메뉴이름, comment 적용
        binding.edtMenu.setText(if(editReview!!.menuName == null){""}else{editReview!!.menuName!!})
        binding.edtMemo.setText(if(editReview!!.comment == null){""}else{editReview!!.comment!!})

        // 해시태그
        for(i in 0.. gustoViewModel.hashTag.size-1) {
            addChip(gustoViewModel.hashTag[i])
        }

        // 이미지 슬라이드
        val imageList = mutableListOf<String>()
        imageList.clear()
        gustoViewModel.reviewEditImg.clear()
        //이미지 처리
        if(!gustoViewModel.myReview!!.img.isNullOrEmpty()){
            imageList.add(gustoViewModel.myReview!!.img!![0])
            setImage(binding.ivFoodImg,gustoViewModel.myReview!!.img!![0],requireContext())
        }

        //공개 비공개
        var publish = false

        //사진 변경
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after th user selects a media item or closes the photo picker.
            if (uri != null) {
                gustoViewModel.reviewEditImg.clear()
                gustoViewModel.reviewEditImg.add(
                    util.convertContentToFile(
                        requireContext(),
                        uri
                    )
                )
                setImage(binding.ivFoodImg,uri.toString(),requireContext())
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.btnSelectImages.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //저장
        binding.btnSave.setOnClickListener {
            //img -> 추후 수정 예정
            var img : String? = null
            //메뉴
            val menu = binding.edtMenu.text.toString()
            //taste
            val taste = binding.ratingbarTasteEdit.rating.toInt()/*
            //spiceness
            val spiceness = binding.ratingbarSpicenessEdit.rating.toInt()
            //mood
            val mood = binding.ratingbarMoodEdit.rating.toInt()
            //toilet
            val toilet = binding.ratingbarToiletEdit.rating.toInt()
            //parking
            val parking = binding.ratingbarParkingEdit.rating.toInt()*/
            //comment
            val comment = binding.edtMemo.text.toString()

            gustoViewModel.editReview(gustoViewModel.myReviewId!!, taste = taste, spiceness = 0, mood = 0, toilet = 0, parking = 0, menuName = menu, comment = comment, img = img,publish = publish){
                result ->
                when(result){
                    0 -> {
                        gustoViewModel.getReview(gustoViewModel.myReviewId!!){
                            result ->
                            when(result){
                                0 -> {
                                    Log.d("img check edit", gustoViewModel.myReview!!.img.toString())
                                    gustoViewModel.changeReviewFlag(true)
                                }
                                1 -> {}
                            }
                        }
                    }
                    1 -> {}
                }
            }
            findNavController().popBackStack()
        }

        binding.btnPrivate.setOnClickListener {
            publish = false
            binding.btnPrivate.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.main_C))
            binding.btnPublic.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.gray_3))
            binding.btnPrivate.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.main_C)
            binding.btnPublic.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray_3)
        }
        binding.btnPublic.setOnClickListener {
            publish = true
            binding.btnPublic.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.main_C))
            binding.btnPrivate.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.gray_3))
            binding.btnPublic.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.main_C)
            binding.btnPrivate.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray_3)
        }

    }
    private fun addChip(text:String) {
        val chip = Chip(requireContext())

        chip.isClickable = true
        chip.isCheckable = true

        chip.text  = text
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_select_color)
        chip.chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.main_C)
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_select_text_color))
        chip.textSize = 12f
        chip.typeface = Typeface.createFromAsset(requireActivity().assets, "font/pretendard_medium.otf")
        chip.chipStrokeWidth = util.dpToPixels(1f, resources.displayMetrics)
        chip.chipCornerRadius = util.dpToPixels(41f, resources.displayMetrics)

        binding.chipGroup.addView(chip)
    }


}