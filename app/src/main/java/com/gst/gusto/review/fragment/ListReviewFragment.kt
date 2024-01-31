package com.gst.gusto.review.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentListReviewBinding
import com.gst.gusto.review.adapter.ListReviewAdapter

class ListReviewFragment : Fragment() {

    lateinit var binding: FragmentListReviewBinding
    lateinit var adapter: ListReviewAdapter

    // 테스트 데이터
    val testDateList = arrayOf(
        "12/03","12/05","12/06",
        "12/08","12/11","12/12",
        "12/16","12/17","12/20",
        "12/22","12/12","12/24",
        "12/25","12/28","12/31"
    )
    val testNameList = arrayOf(
        "구스또 레스토랑", "구스또 식당", "구스또",
        "구스또 레스토랑", "구스또 식당", "구스또 식당",
        "구스또 레스토랑", "구스또", "구스또",
        "구스또 레스토랑", "구스또 식당", "구스또",
        "구스또 레스토랑", "구스또", "구스또 식당"
    )
    val testVisitList = arrayOf(
        "3번 방문", "5번 방문", "10번 방문",
        "3번 방문", "5번 방문", "10번 방문",
        "3번 방문", "5번 방문", "10번 방문",
        "3번 방문", "5번 방문", "10번 방문",
        "3번 방문", "5번 방문", "10번 방문"
    )
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
       binding = FragmentListReviewBinding.inflate(inflater, container, false)

        adapter = ListReviewAdapter(testDateList, testNameList, testVisitList, testImageList)
        binding.apply{
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }
        return binding.root
    }

}