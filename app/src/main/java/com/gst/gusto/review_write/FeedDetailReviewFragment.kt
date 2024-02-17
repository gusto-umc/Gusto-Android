package com.gst.clock.Fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentFeedDetailBinding
import com.gst.gusto.review_write.adapter.HowItem
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter
import com.gst.gusto.review_write.adapter.ReviewHowAdapter
import java.lang.Math.abs

class FeedDetailReviewFragment : Fragment() {

    lateinit var binding: FragmentFeedDetailBinding
    lateinit var  chipGroup: ChipGroup
    private lateinit var scaleUpAnimation: ScaleAnimation
    private lateinit var scaleDownAnimation: ScaleAnimation
    private lateinit var bounceInterpolator: BounceInterpolator
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var page : String
    private var likeIt = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedDetailBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnProfile.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("nickname","mindddy")
            findNavController().navigate(R.id.action_feedDetailReview_to_otherFragment,bundle)
        }
        binding.restInfo.setOnClickListener {
            findNavController().navigate(R.id.action_feedDetailReview_to_storeDetailFragment)
        }
        chipGroup = binding.chipGroup

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 데이터 세팅
        val feedDetail = gustoViewModel.currentFeedData
        binding.tvRestName.text = feedDetail.storeName
        binding.tvRestLoc.text = feedDetail.address
        binding.tvNickname.text = feedDetail.nickName
        setImage(binding.ivProfileImage,feedDetail.profileImage,requireContext())
        binding.tvHeartNum.text = "${feedDetail.likeCnt}"
        val imageList = mutableListOf<String>()
        for(image in feedDetail.images) {
            imageList.add(image)
        }
        binding.tvMenuName.text = feedDetail.menuName
        for(tagNum in feedDetail.hashTags.split(",").map{it.toInt()}) {
            addChip(gustoViewModel.hashTag[tagNum])
        }
        binding.tvMemo.text = feedDetail.comment

        val viewPager = binding.vpImgSlider
        // 이미지 슬라이드
        val adapter = ImageViewPagerAdapter(imageList)
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 2
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER)

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(
            dpToPixels(12f, resources.displayMetrics).toInt()
        ))
        compositePageTransformer.addTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                val r = 1 - abs(position)
                page.alpha = 0.5f + r * 0.5f
            }
        })
        viewPager.setPageTransformer(compositePageTransformer)


        // 평가 리사이클러뷰
        val rv_board = binding.rvHows
        val howList = mutableListOf(feedDetail.taste,feedDetail.spiciness,feedDetail.mood,feedDetail.toilet,feedDetail.parking)
        val howAdapter = ReviewHowAdapter(howList,1)
        howAdapter.notifyDataSetChanged()

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 하트 클릭 리스너
        bounceInterpolator = BounceInterpolator()

        // 확대 애니메이션
        scaleUpAnimation = ScaleAnimation(
            1.0f, 1.2f, 1.0f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleUpAnimation.duration = 500
        scaleUpAnimation.interpolator = bounceInterpolator

        // 축소 애니메이션
        scaleDownAnimation = ScaleAnimation(
            1.2f, 1.0f, 1.2f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleDownAnimation.duration = 500
        scaleDownAnimation.interpolator = bounceInterpolator
        binding.btnHeart.setOnClickListener {
            if (it.isSelected) {
                // 좋아요 취소
                likeIt = 2
                binding.ivHeart.setColorFilter(null)
                it.startAnimation(scaleDownAnimation)
            } else {
                // 좋아요
                likeIt = 1
                val color = ContextCompat.getColor(requireContext(), R.color.main_C)
                binding.ivHeart.setColorFilter(color)
                it.startAnimation(scaleUpAnimation)
            }
            it.isSelected = !it.isSelected
        }
    }

    private fun addChip(text:String) {
        val chip = Chip(requireContext())
        chip.isClickable = false
        chip.isCheckable = false

        chip.text  = text
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.sub_m)
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white))
        chip.chipStrokeWidth = 0f
        chip.textSize = 10f
        chip.typeface = Typeface.createFromAsset(requireActivity().assets, "font/pretendard_bold.otf")
        chip.chipCornerRadius = dpToPixels(41f,resources.displayMetrics)

        // 높이를 20dp로 설정
        val heightInPixels = dpToPixels(45f, resources.displayMetrics)

        // LayoutParams에 높이 설정
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            heightInPixels.toInt()
        )

        // chip에 LayoutParams 할당
        chip.layoutParams = layoutParams

        chipGroup.addView(chip)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(likeIt==1) {
            gustoViewModel.lickReview{ result ->
                when(result) {
                    1 -> {

                    }
                }
            }
        } else if(likeIt==2) {
            gustoViewModel.unlickReview{ result ->
                when(result) {
                    1 -> {

                    }
                }
            }
        }

    }


}