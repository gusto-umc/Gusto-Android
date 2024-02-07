package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.Util.util.Companion.dpToPixels
import com.gst.gusto.databinding.FragmentReviewDetailBinding
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter
import com.gst.gusto.review_write.adapter.ReviewHowAdapter

class ReviewDetailFragment : Fragment() {

    lateinit var binding: FragmentReviewDetailBinding
    lateinit var page : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewDetailBinding.inflate(inflater, container, false)
        val receivedBundle = arguments
        if (receivedBundle != null) {
            page = receivedBundle.getString("page")?:"review"
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_reviewDetail_to_reviewDetailEdit,receivedBundle)
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
            R.drawable.review_gallery_test,
            R.drawable.review_gallery_test2,
            R.drawable.review_gallery_test
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

        binding.btnPopup.setOnClickListener {
            binding.lyEditRemove.visibility = View.VISIBLE
        }

    }



}