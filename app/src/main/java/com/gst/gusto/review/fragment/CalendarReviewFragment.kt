package com.gst.gusto.review.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentCalendarReviewBinding
import com.gst.gusto.review.adapter.CalendarReviewAdapter


class CalendarReviewFragment : Fragment() {

    lateinit var binding: FragmentCalendarReviewBinding
    lateinit var adapter: CalendarReviewAdapter

    // 테스트 이미지의 id
    val testImageList = arrayOf(
        R.drawable.review_gallery_test, R.drawable.review_gallery_test2, R.drawable.review_gallery_test,
        R.drawable.review_gallery_test, R.drawable.review_gallery_test, R.drawable.review_gallery_test2,
        R.drawable.review_gallery_test2, R.drawable.review_gallery_test, R.drawable.review_gallery_test,
        R.drawable.review_gallery_test, R.drawable.review_gallery_test2, R.drawable.review_gallery_test2,
        R.drawable.review_gallery_test2, 0, R.drawable.review_gallery_test2,
        R.drawable.review_gallery_test, R.drawable.review_gallery_test2, R.drawable.review_gallery_test,
        0, R.drawable.review_gallery_test, R.drawable.review_gallery_test2,
        R.drawable.review_gallery_test2, R.drawable.review_gallery_test, R.drawable.review_gallery_test,
        R.drawable.review_gallery_test, 0, R.drawable.review_gallery_test2,
        R.drawable.review_gallery_test2, R.drawable.review_gallery_test, 0,
        R.drawable.review_gallery_test, R.drawable.review_gallery_test2, R.drawable.review_gallery_test2,
        0, R.drawable.review_gallery_test
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarReviewBinding.inflate(inflater, container, false)

        // 클릭 리스너 부분

        adapter = CalendarReviewAdapter(testImageList, context,
            itemClickListener = {
                val bundle = Bundle()
                bundle.putInt("reviewId",0)     //리뷰 아이디 넘겨 주면 됨
                bundle.putString("page","review")
                findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail,bundle)
            })
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(activity, 7)
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }
}