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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedDetailBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_feedDetailReview_to_storeDetailFragment)
        }
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_feedDetailReview_to_storeDetailFragment)
        }
        chipGroup = binding.chipGroup

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.vpImgSlider
        val imageList = listOf(
            R.drawable.sample_store_img,
            R.drawable.sample_store_2_img,
            R.drawable.sample_store_3_img
            // Add more images as needed
        )


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

        // 해쉬태그
        addChip("# 안매운")
        addChip("# 분위기")
        addChip("# 화장실")
        addChip("# 주차장")
        addChip("# 가성비")

        // 평가 리사이클러뷰
        val rv_board = binding.rvHows
        val howList = mutableListOf(3,3,3,3,3)
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
                binding.ivHeart.setColorFilter(null)
                it.startAnimation(scaleDownAnimation)
            } else {
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


}