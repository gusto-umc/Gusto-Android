package com.gst.clock.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.clearFragmentResult
import androidx.lifecycle.Observer
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

        //1. argument 저장
        var reviewId = arguments?.getLong("reviewId")
        Log.d("review detail check", reviewId.toString())
        gustoViewModel.myReviewId = reviewId

        //2. 서버 연결
        gustoViewModel.getReview(gustoViewModel.myReviewId!!){
            result ->
            when(result){
                0 -> {
                    //success
                    //데이터 적용 - 날짜
                    val reviewDate = LocalDate.parse(gustoViewModel.myReview!!.visitedAt)
                    binding.tvDay.text = "${reviewDate.year}. ${reviewDate.monthValue}. ${reviewDate.dayOfMonth}"
                    //데이터 적용 - 가게명
                    binding.tvReviewStoreName.text = gustoViewModel.myReview!!.storeName
                    binding.lyTitle.setOnClickListener {
                        gustoViewModel.selectedDetailStoreId = gustoViewModel.myReview!!.storeId.toInt()
                        findNavController().navigate(R.id.action_reviewDetail_to_storeDetailFragment)
                    }
                    //데이터 적용 - 하트수
                    binding.tvHeartNum.text = gustoViewModel.myReview!!.likeCnt.toString()
                    //데이터 적용 - 메뉴
                    binding.tvMenu.text = if(gustoViewModel.myReview!!.menuName.isNullOrBlank()){
                        ""
                    } else{
                        gustoViewModel.myReview!!.menuName
                    }
                    //taste 처리
                    binding.ratingbarTaste.rating = gustoViewModel.myReview!!.taste.toFloat()
                    //spiciness 처리
                    binding.ratingbarSpiceness.rating = gustoViewModel.myReview!!.spiciness!!.toFloat()
                    //mood 처리
                    binding.ratingbarMood.rating = gustoViewModel.myReview!!.mood!!.toFloat()
                    //toilet 처리
                    binding.ratingbarToilet.rating = gustoViewModel.myReview!!.toilet!!.toFloat()
                    //parking 처리-> 더미데이터가 null이라서 임의 처리, 추후 보완 예정
                    binding.ratingbarParking.rating = gustoViewModel.myReview!!.parking!!.toFloat()
                    //comment 처리
                    binding.tvMemo.text = if(gustoViewModel.myReview!!.comment == null){
                        ""
                    } else{
                        gustoViewModel.myReview!!.comment
                    }
                    gustoViewModel.changeReviewFlag(true)

                }
                1 -> {
                    //fail
                    Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 이미지 슬라이드 기본 설정
          */
        val imageList = mutableListOf<String>(
                ""
        )

        fun settingImages(imageList : List<String>){
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
        gustoViewModel.successFlg.observe(viewLifecycleOwner, Observer{
            if(it){
                if(!gustoViewModel.myReview!!.img.isNullOrEmpty()){
                    settingImages(gustoViewModel.myReview!!.img!!)
                    Log.d("img check", gustoViewModel.myReview!!.img.toString())
                } else{
                    settingImages(imageList)
                    Log.d("img check", "null입니다.")
                }
            }

        })



        /**
         * 메뉴바 클릭 리스너
         */
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