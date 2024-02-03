package com.gst.clock.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMyReviewBinding
import com.gst.gusto.review.adapter.GalleryReviewAdapter

class MyReviewFragment : Fragment() {

    lateinit var binding: FragmentMyReviewBinding
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
        binding = FragmentMyReviewBinding.inflate(inflater, container, false)

        // 클릭 리스너 부분

        adapter = GalleryReviewAdapter(testImageList, context,
            itemClickListener = {
                it -> Toast.makeText(context,"테스트용", Toast.LENGTH_SHORT).show()
        })
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
            adapter.notifyDataSetChanged()
        }

        return binding.root

    }

}