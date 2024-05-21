package com.gst.gusto.other

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMyReviewBinding
import com.gst.gusto.review.adapter.InstaReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration

class OtherReviewFragment : Fragment() {

    lateinit var binding: FragmentMyReviewBinding
    lateinit var adapter: InstaReviewAdapter

    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyReviewBinding.inflate(inflater, container, false)

        initView()
        // getData()

        return binding.root

    }

    fun initView(){

        /*adapter = InstaReviewAdapter(ArrayList(), context
        ) { reviewId ->
            *//*val bundle = Bundle()
            bundle.putLong("reviewId", reviewId)     //리뷰 아이디 넘겨 주면 됨
            bundle.putString("page","review")*//*

            // gustoViewModel.currentFeedReviewId = reviewId
            gustoViewModel.getFeedReview { result ->
                when (result) {
                    1 -> {
                        findNavController().navigate(R.id.action_otherFragment_to_feedDetail)
                    }
                }
            }
        }*/

        binding.apply {
            // 클릭 리스너 부분
            recyclerView.adapter = adapter
            val size = resources.getDimensionPixelSize(R.dimen.one_dp)
            val color = Color.WHITE
            val itemDecoration = GridItemDecoration(size, color)
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
        }
    }


    /*fun getData() {
        gustoViewModel.otherInstaView(gustoViewModel.currentFeedNickname, null, 30) { result, response ->
            if (result == 1) {
                val galleryList = ArrayList<ResponseInstaReviews>()
                response?.reviews?.forEach { review ->
                    galleryList.add(ResponseInstaReviews(review.reviewId, review.images))
                }
                adapter.galleryList = galleryList
                adapter.notifyDataSetChanged()
            }
            Log.d("otherResponse", response.toString())
        }
    }*/

}