package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.databinding.FragmentReviewDetailBinding
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter
import com.gst.gusto.review_write.adapter.ReviewHowAdapter

class ReviewDetailFragment : Fragment() {

    lateinit var binding: FragmentReviewDetailBinding

    private lateinit var scaleUpAnimation: ScaleAnimation
    private lateinit var scaleDownAnimation: ScaleAnimation
    private lateinit var bounceInterpolator: BounceInterpolator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewDetailBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reviewDetail_to_myFragment)
        }
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_reviewDetail_to_reviewDetailEdit)
        }
        binding.btnRemove.setOnClickListener {

        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // 평가 리사이클러뷰
        val rv_board = binding.rvHows
        val howList = mutableListOf(3,3,3,3,3)
        val howAdapter = ReviewHowAdapter(howList,2)
        howAdapter.notifyDataSetChanged()

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        // 이미지 슬라이드
        val viewPager = binding.vpImgSlider
        val imageList = listOf(
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background
            // Add more images as needed
        )

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

        binding.btnPopup.setOnClickListener {
            binding.lyEditRemove.visibility = View.VISIBLE
        }

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



}