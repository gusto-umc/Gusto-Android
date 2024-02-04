package com.gst.gusto.review.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentGalleryReviewBinding
import com.gst.gusto.review.adapter.GalleryReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration

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

        // 클릭 리스너 부분

        adapter = GalleryReviewAdapter(testImageList, context,
            itemClickListener = {
                val bundle = Bundle()
                bundle.putInt("reviewId",0)     //리뷰 아이디 넘겨 주면 됨
                findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail,bundle)
            })
        binding.apply {
            recyclerView.adapter = adapter
            val size = resources.getDimensionPixelSize(R.dimen.one_dp)
            val color = Color.WHITE
            val itemDecoration = GridItemDecoration(size, color)
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

}