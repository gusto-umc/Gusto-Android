package com.gst.gusto.review.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseListReview
import com.gst.gusto.databinding.FragmentListReviewBinding
import com.gst.gusto.review.adapter.ListReviewAdapter
import com.gst.gusto.review.adapter.ListReviewData
import com.gst.gusto.review.adapter.ListReviewType
import java.text.SimpleDateFormat
import java.util.Locale

class ListReviewFragment : Fragment() {

    lateinit var binding: FragmentListReviewBinding
    lateinit var adapter: ListReviewAdapter
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       binding = FragmentListReviewBinding.inflate(inflater, container, false)

        // 클릭 리스너 부분
        adapter = ListReviewAdapter(itemClickListener = {
            if(it.viewType == ListReviewType.LISTREVIEW){
                val bundle = Bundle()
                bundle.putLong("reviewId", it.reviewId)     //리뷰 아이디 넘겨 주면 됨
                bundle.putString("page","review")
                findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail,bundle)
            } else {
                // 리뷰 작성 버튼
            }

        })

        binding.apply{
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }

        gustoViewModel.timeLineView(null, 31) { result , response ->
            if(result == 1){
                adapter.setData(list = getData(response))
            }
        }
        
        return binding.root
    }

    fun getData(response: ResponseListReview?): ArrayList<ListReviewData> {
        val reviewList: ArrayList<ListReviewData> = ArrayList()
        val DF = SimpleDateFormat("MM/dd", Locale.KOREAN)
        Log.d("response", response?.reviews.toString())

        var imageview1 = ""
        var imageview2 = ""
        var imageview3 = ""

        response?.reviews?.let {
            for(data in it){
                val date = DF.format(data.visitedAt).toString()
                val name = data.storeName
                val visit = data.visitedCount.toString()
                // Log.d("img", data.imgs.toString())
                if(data.images != null){
                    imageview1 = data.images.getOrNull(0).toString()
                    imageview2 = data.images.getOrNull(1).toString()
                    imageview3 = data.images.getOrNull(2).toString()
                }


                val reviewData = ListReviewData(date, name, visit, imageview1, imageview2, imageview3, data.reviewId)
                reviewList.add(reviewData)
            }
        }

        val NowTime = System.currentTimeMillis()
        val result = DF.format(NowTime).toString()
        reviewList.add(ListReviewData(result, "","","","","", 0, ListReviewType.LISTBUTTON))

        return reviewList
    }

}