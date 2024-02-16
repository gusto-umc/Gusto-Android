package com.gst.gusto.review.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseInstaReviews
import com.gst.gusto.databinding.FragmentGalleryReviewBinding
import com.gst.gusto.review.adapter.GalleryReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration

class GalleryReviewFragment : Fragment() {

    lateinit var binding: FragmentGalleryReviewBinding
    lateinit var adapter: GalleryReviewAdapter

    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGalleryReviewBinding.inflate(inflater, container, false)

        initView()
        getData()

        return binding.root
    }

    fun initView(){

        adapter = GalleryReviewAdapter(ArrayList(), context,
            itemClickListener = { reviewId ->
                val bundle = Bundle()
                bundle.putLong("reviewId", reviewId)     //리뷰 아이디 넘겨 주면 됨
                bundle.putString("page","review")
                findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail,bundle)
            })

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


    fun getData() {
        gustoViewModel.getTokens(requireActivity() as MainActivity)
        gustoViewModel.instaView(null, 31) { result, response ->
            if (result == 1) {
                val galleryList = ArrayList<ResponseInstaReviews>()
                response?.reviews?.forEach { review ->
                    galleryList.add(ResponseInstaReviews(review.reviewId, review.images))
                }
                adapter.galleryList = galleryList
                adapter.notifyDataSetChanged()
            }
            Log.d("listResponse", response.toString())
        }
    }

}