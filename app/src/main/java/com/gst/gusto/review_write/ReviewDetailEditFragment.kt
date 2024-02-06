package com.gst.clock.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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
import com.gst.gusto.databinding.FragmentReviewDetailEditBinding
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter
import com.gst.gusto.review_write.adapter.ReviewHowAdapter

class ReviewDetailEditFragment : Fragment() {

    lateinit var binding: FragmentReviewDetailEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewDetailEditBinding.inflate(inflater, container, false)

        val receivedBundle = arguments

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reviewDetailEdit_to_reviewDetailFragment,receivedBundle)
        }
        binding.btnSave.setOnClickListener {
            findNavController().navigate(R.id.action_reviewDetailEdit_to_reviewDetailFragment,receivedBundle)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // 평가 리사이클러뷰
        val rv_board = binding.rvHows
        val howList = mutableListOf(3,3,3,3,3)
        val howAdapter = ReviewHowAdapter(howList,3)
        howAdapter.notifyDataSetChanged()

        rv_board.adapter = howAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(4)) { uri ->
            // Callback is invoked after th user selects a media item or closes the photo picker.
            if (uri != null) {
                for (j in 0 .. uri.size-1) {
                    adapter.setImageView(j,uri[j].toString(),requireContext())
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

    }



}