package com.gst.gusto.review.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentListReviewBinding
import com.gst.gusto.review.adapter.ListReviewAdapter
import com.gst.gusto.review.adapter.ListReviewData
import com.gst.gusto.review.adapter.ListReviewType
import java.text.SimpleDateFormat
import java.util.Locale

class ListReviewFragment : Fragment() {

    lateinit var binding: FragmentListReviewBinding
    lateinit var adapter: ListReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       binding = FragmentListReviewBinding.inflate(inflater, container, false)

        // 클릭 리스너 부분
        adapter = ListReviewAdapter(itemClickListener = {
            val bundle = Bundle()
            bundle.putInt("reviewId",0)     //리뷰 아이디 넘겨 주면 됨
            findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail,bundle)
        })

        binding.apply{
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }

        adapter.setData(list = testData())
        Log.d("testData", testData().toString())

        return binding.root
    }

    // 테스트 데이터 세팅
    fun testData(): ArrayList<ListReviewData> {
        var reviewList: ArrayList<ListReviewData> = ArrayList()

        // 테스트 데이터
        val testDateList = arrayOf(
            "12/03","12/05","12/06",
            "12/08","12/11","12/12",
            "12/16","12/17","12/20",
            "12/22","12/12","12/24",
            "12/25","12/28","12/30"
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
            R.drawable.review_gallery_test, R.drawable.review_gallery_test2, R.drawable.review_gallery_test,
            R.drawable.review_gallery_test, R.drawable.review_gallery_test, R.drawable.review_gallery_test2,
            R.drawable.review_gallery_test2, R.drawable.review_gallery_test, R.drawable.review_gallery_test,
            R.drawable.review_gallery_test, R.drawable.review_gallery_test2, R.drawable.review_gallery_test2,
            R.drawable.review_gallery_test2, R.drawable.review_gallery_test, R.drawable.review_gallery_test2,
        )

        for(i in 0 .. 14){
            reviewList.add(ListReviewData(testDateList[i], testNameList[i], testVisitList[i], testImageList[i], testImageList[i], testImageList[i], ListReviewType.LISTREVIEW))
        }
        val NowTime = System.currentTimeMillis()
        val DF = SimpleDateFormat("MM/dd", Locale.KOREAN)
        val result = DF.format(NowTime).toString()

        reviewList.add(ListReviewData(result, "","",0,0,0, ListReviewType.LISTBUTTON))

        return reviewList
    }

}