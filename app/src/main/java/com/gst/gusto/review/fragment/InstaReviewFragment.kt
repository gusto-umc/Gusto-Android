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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentGalleryReviewBinding
import com.gst.gusto.review.adapter.InstaReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration
import com.gst.gusto.review.viewmodel.ReviewViewModel
import com.gst.gusto.review.viewmodel.ReviewViewModelFactory
import com.gst.gusto.util.ScrollUtil.addOnScrollEndListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InstaReviewFragment : Fragment() {

    lateinit var binding: FragmentGalleryReviewBinding

    private val adapter: InstaReviewAdapter by lazy {
        InstaReviewAdapter(context) { reviewId ->
            val bundle = Bundle()
            bundle.putLong("reviewId", reviewId)
            bundle.putString("page", "review")
            findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail, bundle)
        }
    }


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

    fun initView() {

        binding.apply {
            // 클릭 리스너 부분
            recyclerView.adapter = adapter
            val size = resources.getDimensionPixelSize(R.dimen.one_dp)
            val color = Color.WHITE
            val itemDecoration = GridItemDecoration(size, color)
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
            adapter.addLoading()
        }

        viewModel.instaReviews.observe(viewLifecycleOwner){
            lifecycleScope.launch {
                delay(2000)
                adapter.addItems(it)
                adapter.addLoading()
            }
        }
    }



fun pagingRecyclerview(){
    binding.recyclerView.addOnScrollEndListener {
        viewModel.onScrolled()
    }
    viewModel.scrollData.observe(viewLifecycleOwner){
        lifecycleScope.launch {
            delay(2000)
            adapter.removeLoading()
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
