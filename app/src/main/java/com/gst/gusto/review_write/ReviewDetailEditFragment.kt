package com.gst.clock.Fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
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
import java.io.File
import java.time.LocalDate

class ReviewDetailEditFragment : Fragment() {

    lateinit var binding: FragmentReviewDetailEditBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    private lateinit var activityResult: ActivityResultLauncher<Intent>
    private val imageList = mutableListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewDetailEditBinding.inflate(inflater, container, false)

        val receivedBundle = arguments

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        activityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("testtest","good")
                imageList.clear()
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
                            imageList.add(imageUri)
                        }
                    } else {
                        val imageUri = it.data
                        imageList.add(imageUri!!)
                    }
                    setImage(binding.ivFoodImg,imageList.get(0).toString(),requireContext())
                }
            }
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
        //이미지 처리
        if(!gustoViewModel.myReview!!.img.isNullOrEmpty()){
            setImage(binding.ivFoodImg,gustoViewModel.myReview!!.img!![0],requireContext())
        }

        //공개 비공개
        var publish = false

        //사진 변경

        binding.btnSelectImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            activityResult.launch(intent)
        }

        //저장
        binding.btnSave.setOnClickListener {
            //사진
            val imgFiles = ArrayList<File>()
            for(img in imageList) {
                imgFiles.add(util.convertContentToFile(requireContext(),img))
            }
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


            gustoViewModel.editReview(gustoViewModel.myReviewId!!, taste = taste, spiceness = 0, mood = 0, toilet = 0, parking = 0, menuName = menu, comment = comment, imgFiles = imgFiles,publish = publish){
                result ->
                when(result){
                    0 -> {
                        gustoViewModel.getReview(gustoViewModel.myReviewId!!){
                            result ->
                            when(result){
                                0 -> {
                                    Log.d("img check edit", gustoViewModel.myReview!!.img.toString())
                                    gustoViewModel.changeReviewFlag(true)
                                    findNavController().popBackStack()
                                }
                                1 -> {}
                            }
                        }
                    }
                    1 -> {}
                }
            }

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

        // 공개 비공개 적용
        if(gustoViewModel.myReview!!.publicCheck) binding.btnPublic.callOnClick()
        else binding.btnPrivate.callOnClick()
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