package com.gst.gusto.feed

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
import com.gst.gusto.databinding.FragmentFeedBinding
import com.gst.gusto.review.adapter.GalleryReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration

class FeedFragment : Fragment() {

    lateinit var binding: FragmentFeedBinding
    lateinit var adapter: GalleryReviewAdapter

    // 테스트 이미지의 id
    val testImageList = arrayOf(
       "hello"
    )

    val bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        /*// feed 이미지클릭 리스너 부분
        adapter = GalleryReviewAdapter(testImageList, context,
            itemClickListener = {
                val bundle = Bundle()
                bundle.putInt("reviewId",0)     //리뷰 아이디 넘겨 주면 됨
                bundle.putString("page","feed")
                findNavController().navigate(R.id.action_feedFragment_to_feedDetailReviewFragment,bundle)
            })*/

        binding.apply {
            recyclerView.adapter = adapter
            val size = resources.getDimensionPixelSize(R.dimen.one_dp)
            val color = Color.WHITE
            val itemDecoration = GridItemDecoration(size, color)
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
            adapter.notifyDataSetChanged()

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

        return binding.root

    }

}