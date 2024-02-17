package com.gst.gusto.feed

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseFeedReview
import com.gst.gusto.api.ResponseInstaReviews
import com.gst.gusto.databinding.FragmentFeedBinding
import com.gst.gusto.review.adapter.GalleryReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration

class FeedFragment : Fragment() {

    lateinit var binding: FragmentFeedBinding
    lateinit var adapter: FeedReviewAdapter

    private val gustoViewModel : GustoViewModel by activityViewModels()

    val bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        initView()
        getData()

        return binding.root

    }

    fun initView(){

        adapter = FeedReviewAdapter(ArrayList(), context,
            itemClickListener = { reviewId ->
                val bundle = Bundle()
                bundle.putLong("reviewId", reviewId)     //리뷰 아이디 넘겨 주면 됨
                bundle.putString("page","feed")
                findNavController().navigate(R.id.action_feedFragment_to_feedDetailReviewFragment,bundle)
            })

        binding.apply {
            recyclerView.adapter = adapter
            val size = resources.getDimensionPixelSize(R.dimen.one_dp)
            val color = Color.WHITE
            val itemDecoration = GridItemDecoration(size, color)
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.layoutManager = GridLayoutManager(activity, 3)

            // editText 포커스 있을때 FeedSearchFragment로 이동
            feedEditText.apply{
                setOnFocusChangeListener { view, focus ->
                    if (focus){
                        val fragmentManger = activity?.supportFragmentManager
                        fragmentManger?.beginTransaction()
                            ?.add(R.id.fl_container, FeedSearchFragment())
                            ?.addToBackStack(null)
                            ?.commit()
                        clearFocus()
                    }
                }
            }
        }
    }


    fun getData() {
        gustoViewModel.getTokens(requireActivity() as MainActivity)
        gustoViewModel.feed() { result, response ->
            if (result == 1) {
                val feedList = ArrayList<ResponseFeedReview>()

                response?.forEach {
                    feedList.add(ResponseFeedReview(it.reviewId, it.images))
                }
                adapter.feedList = feedList
                adapter.notifyDataSetChanged()
            }
            Log.d("listResponse", response.toString())
        }
    }

}