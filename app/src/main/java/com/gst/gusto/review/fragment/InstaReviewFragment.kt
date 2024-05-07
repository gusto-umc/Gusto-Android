package com.gst.gusto.review.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentGalleryReviewBinding
import com.gst.gusto.review.adapter.InstaReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration
import com.gst.gusto.review.viewmodel.ReviewViewModel
import com.gst.gusto.review.viewmodel.ReviewViewModelFactory
import com.gst.gusto.util.GustoApplication
import kotlinx.coroutines.launch

class InstaReviewFragment : Fragment() {

    lateinit var binding: FragmentGalleryReviewBinding
    lateinit var adapter: InstaReviewAdapter

    private val viewModel: ReviewViewModel by viewModels( ownerProducer = { requireParentFragment()}, factoryProducer = { ReviewViewModelFactory() } )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGalleryReviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setToast()
        pagingRecyclerview()
    }

    fun initView(){

        viewModel.instaReviews.observe(viewLifecycleOwner){
            adapter = InstaReviewAdapter(it, context
            ) { position ->
                val bundle = Bundle()
                bundle.putLong(
                    "reviewId",
                    viewModel.instaReviews.value?.get(position)?.reviewId ?: -1
                )     //리뷰 아이디 넘겨 주면 됨
                Log.d("reviews", "${viewModel.instaReviews.value}")
                bundle.putString("page", "review")
                findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail, bundle)
            }
            binding.apply {
                // 클릭 리스너 부분
                recyclerView.adapter = adapter
                var count = 0
                viewModel.instaReviews.value?.forEach { review ->
                    review?.let {
                        Log.d("reviews", "Review ID: ${count} + ${it.reviewId}")
                        count++
                    }
                }
                val size = resources.getDimensionPixelSize(R.dimen.one_dp)
                val color = Color.WHITE
                val itemDecoration = GridItemDecoration(size, color)
                recyclerView.addItemDecoration(itemDecoration)
                recyclerView.layoutManager = GridLayoutManager(activity, 3)
                adapter.addLoading()
            }
        }
    }


    fun pagingRecyclerview(){
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var flag = 0

                val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)?.findLastCompletelyVisibleItemPosition()

                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                val totalCount = recyclerView.adapter?.itemCount?.minus(1)
                    // 페이징 처리
                viewModel.instaPagingdata.observe(viewLifecycleOwner){
                    if(rvPosition == totalCount && it.second == true && flag == 0) {
                        getReviewPaging()
                        flag = 1
                    }
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        it.second?.takeIf { !it }?.let {
                            adapter.removeLoading()
                        }
                    }, 3000)
                }
            }
        })
    }

    fun getReviewPaging() {

        val cursorId = viewModel.instaPagingdata.value?.first
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.addItems(viewModel.getInstaReviewPaging(cursorId, 18))
            var count = 0
            viewModel.instaReviews.value?.forEach { review ->
                review?.let {
                    Log.d("reviews", "Review ID: ${count} + ${it.reviewId}")
                    count++
                }
            }
        }

    }

    fun setToast(){
        viewModel.tokenToastData.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), "토큰을 재 발급 중입니다", Toast.LENGTH_SHORT).show()
        }
        viewModel.errorToastData.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
        }
    }
}