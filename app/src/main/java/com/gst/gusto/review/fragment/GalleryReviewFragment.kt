package com.gst.gusto.review.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentGalleryReviewBinding
import com.gst.gusto.review.adapter.GalleryReviewAdapter

class GalleryReviewFragment : Fragment() {

    lateinit var binding: FragmentGalleryReviewBinding
    lateinit var adapter: GalleryReviewAdapter

    // 테스트 이미지의 id
    val testImageList = arrayOf(
        R.drawable.review_gallery_test, R.drawable.review_gallery_test2, R.drawable.review_gallery_test,
        R.drawable.review_gallery_test, R.drawable.review_gallery_test, R.drawable.review_gallery_test2,
        R.drawable.review_gallery_test2, R.drawable.review_gallery_test, R.drawable.review_gallery_test,
        R.drawable.review_gallery_test, R.drawable.review_gallery_test2, R.drawable.review_gallery_test2,
        R.drawable.review_gallery_test2, R.drawable.review_gallery_test, R.drawable.review_gallery_test2,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryReviewBinding.inflate(inflater, container, false)

        adapter = GalleryReviewAdapter(testImageList, context)
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

}