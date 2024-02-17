package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.clearFragmentResult
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseMyReview
import com.gst.gusto.databinding.FragmentReviewDetailBinding
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter
import com.gst.gusto.review_write.adapter.ReviewHowAdapter
import java.time.LocalDate

class ReviewDetailFragment : Fragment() {

    lateinit var binding: FragmentReviewDetailBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var page : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewDetailBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.lyTitle.setOnClickListener {
            findNavController().navigate(R.id.action_reviewDetail_to_storeDetailFragment)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gustoViewModel.getTokens(requireActivity() as MainActivity)

        /**
         * 이미지 슬라이드 기본 설정
          */
        val imageList = mutableListOf<Int>(
            R.drawable.review_gallery_test,
            R.drawable.review_gallery_test2,
            R.drawable.review_gallery_test
            // Add more images as needed
        )

        fun settingImages(imageList : MutableList<Int>){
            val viewPager = binding.vpImgSlider

            val adapter = ImageViewPagerAdapter(imageList)
            viewPager.adapter = adapter

            viewPager.offscreenPageLimit = 4
            viewPager.clipToPadding = false
            viewPager.clipChildren = false
            viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER)

            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(
                MarginPageTransformer(
                    dpToPixels(12f, resources.displayMetrics).toInt()
                )
            )
            compositePageTransformer.addTransformer(object : ViewPager2.PageTransformer {
                override fun transformPage(page: View, position: Float) {
                    val r = 1 - Math.abs(position)
                    page.alpha = 0.5f + r * 0.5f
                }
            })
            viewPager.setPageTransformer(compositePageTransformer)

        }


        /**
         * 서버 데이터 연결
         */
        var reviewId = 1
        gustoViewModel.myReviewId = reviewId.toLong()

        gustoViewModel.getReview(reviewId.toLong()){
            result ->
            when(result){
                0 -> {
                    if(gustoViewModel.myReview != null){
                        val reviewDate = LocalDate.parse(gustoViewModel.myReview!!.visitedAt)
                        binding.tvDay.text = "${reviewDate.year}. ${reviewDate.monthValue}. ${reviewDate.dayOfMonth}"
                        binding.tvReviewStoreName.text = gustoViewModel.myReview!!.storeName
                        binding.tvHeartNum.text = gustoViewModel.myReview!!.likeCnt.toString()
                        //이미지 처리
                        if(!gustoViewModel.myReview!!.img.isNullOrEmpty()){
                            var reviewImageList : MutableList<Int>? = null

                        }
                        else{
                            settingImages(imageList)
                        }
                        //메뉴
                        binding.tvMenu.text = if(gustoViewModel.myReview!!.menuName.isNullOrBlank()){
                            ""
                        }
                        else{
                            gustoViewModel.myReview!!.menuName
                        }
                        //taste 처리
                        binding.ratingbarTaste.rating = gustoViewModel.myReview!!.taste.toFloat()
                        //spiceness 처리
                        if(gustoViewModel.myReview!!.spiciness == null){
                            binding.ratingbarSpiceness.visibility = View.INVISIBLE
                        }
                        else{
                            binding.ratingbarSpiceness.visibility = View.VISIBLE
                            binding.ratingbarSpiceness.rating = gustoViewModel.myReview!!.taste.toFloat()
                        }
                        //mood 처리
                        if(gustoViewModel.myReview!!.mood == null){
                            binding.ratingbarMood.visibility = View.INVISIBLE
                        }
                        else{
                            binding.ratingbarMood.visibility = View.VISIBLE
                            binding.ratingbarMood.rating = gustoViewModel.myReview!!.mood!!.toFloat()
                        }
                        //toilet 처리
                        if(gustoViewModel.myReview!!.toilet == null){
                            binding.ratingbarToilet.visibility = View.INVISIBLE
                        }
                        else{
                            binding.ratingbarToilet.visibility = View.VISIBLE
                            binding.ratingbarToilet.rating = gustoViewModel.myReview!!.toilet!!.toFloat()
                        }
                        //parking 처리-> 더미데이터가 null이라서 임의 처리, 추후 보완 예정
                        if(gustoViewModel.myReview!!.parking == null){
                            binding.ratingbarParking.visibility = View.INVISIBLE
                        }
                        else{
                            binding.ratingbarParking.visibility = View.VISIBLE
                            binding.ratingbarParking.rating = gustoViewModel.myReview!!.parking!!.toFloat()
                        }
                        //comment 처리
                        binding.tvMemo.text = if(gustoViewModel.myReview!!.comment == null){
                            ""
                        }
                        else{
                            gustoViewModel.myReview!!.comment
                        }

                    }else{
                        Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                1 -> {
                    Toast.makeText(context, "리뷰 상세 GET 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.btnPopup.setOnClickListener {
            if(binding.lyEditRemove.isGone){
                binding.lyEditRemove.visibility = View.VISIBLE
            }
            else{
                binding.lyEditRemove.visibility = View.GONE
            }
        }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_reviewDetail_to_reviewDetailEdit)
        }
        binding.btnRemove.setOnClickListener {
            gustoViewModel.deleteReview(reviewId = gustoViewModel.myReviewId!!){
                result ->
                when(result){
                    0 -> {
                        //성공
                        Toast.makeText(context, "리뷰를 성공적으로 삭제했습니다.", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(view).popBackStack()
                    }
                    1 -> {
                        //실페
                        Toast.makeText(context, "리뷰 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }



}