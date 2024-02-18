package com.gst.clock.Fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.isPhotoPickerAvailable
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewDetailEditBinding
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter
import com.gst.gusto.review_write.adapter.ReviewHowAdapter
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
        binding.ratingbarSpicenessEdit.rating = if(editReview!!.spiciness == null){ 3.0F } else{ editReview!!.spiciness!!.toFloat() }
        binding.ratingbarMoodEdit.rating = if(editReview!!.mood == null){ 3.0F } else{editReview!!.mood!!.toFloat()}
        binding.ratingbarToiletEdit.rating = if(editReview!!.toilet == null ){3.0F}else{editReview!!.toilet!!.toFloat()}
        binding.ratingbarParkingEdit.rating = if(editReview!!.parking == null){3.0F} else{editReview!!.parking!!.toFloat()}

        //메뉴이름, comment 적용
        binding.edtMenu.setText(if(editReview!!.menuName == null){""}else{editReview!!.menuName!!})
        binding.edtMemo.setText(if(editReview!!.comment == null){""}else{editReview!!.comment!!})



        // 이미지 슬라이드
        val viewPager = binding.vpImgSlider
        val imageList = mutableListOf<String>()
        imageList.clear()
        gustoViewModel.reviewEditImg.clear()
        //이미지 처리
        if(!gustoViewModel.myReview!!.img.isNullOrEmpty()){
            for(i in gustoViewModel.myReview!!.img!!){
                imageList.add(i)
            }
        }


        val adapter = ImageViewPagerAdapter(imageList)
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 2
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER)

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(
            MarginPageTransformer(
                util.dpToPixels(12f, resources.displayMetrics).toInt()
            )
        )
        compositePageTransformer.addTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                val r = 1 - Math.abs(position)
                page.alpha = 0.5f + r * 0.5f
            }
        })
        viewPager.setPageTransformer(compositePageTransformer)



        //사진 변경
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(4)) { uri ->
            // Callback is invoked after th user selects a media item or closes the photo picker.
            if (uri != null) {
                for (j in 0 .. uri.size-1) {
                    adapter.setImageView(j,uri[j].toString(),requireContext())
                    gustoViewModel.reviewEditImg.clear()
                    gustoViewModel.reviewEditImg.add(
                        util.convertContentToFile(
                            requireContext(),
                            uri[j]
                        )
                    )
                }

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.btnSelectImages.setOnClickListener {
            if(isPhotoPickerAvailable()) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        //저장
        binding.btnSave.setOnClickListener {
            //img -> 추후 수정 예정
            var img : String? = null
            //메뉴
            val menu = binding.edtMenu.text.toString()
            //taste
            val taste = binding.ratingbarTasteEdit.rating.toInt()
            //spiceness
            val spiceness = binding.ratingbarSpicenessEdit.rating.toInt()
            //mood
            val mood = binding.ratingbarMoodEdit.rating.toInt()
            //toilet
            val toilet = binding.ratingbarToiletEdit.rating.toInt()
            //parking
            val parking = binding.ratingbarParkingEdit.rating.toInt()
            //comment
            val comment = binding.edtMemo.text.toString()


            gustoViewModel.editReview(gustoViewModel.myReviewId!!, taste = taste, spiceness = spiceness, mood = mood, toilet = toilet, parking = parking, menuName = menu, comment = comment, img = img){
                result ->
                when(result){
                    0 -> {}
                    1 -> {}
                }
            }
            findNavController().popBackStack()
        }

    }



}