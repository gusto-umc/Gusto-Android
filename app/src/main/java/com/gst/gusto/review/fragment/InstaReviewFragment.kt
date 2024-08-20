package com.gst.gusto.review.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentInstaReviewBinding
import com.gst.gusto.review.adapter.InstaReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration
import com.gst.gusto.review.viewmodel.InstaReviewViewModel
import com.gst.gusto.review.viewmodel.InstaReviewViewModelFactory
import com.gst.gusto.util.ScrollUtil.addFabOnScrollListener
import com.gst.gusto.util.ScrollUtil.addGridOnScrollEndListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InstaReviewFragment : Fragment() {

    lateinit var binding: FragmentInstaReviewBinding

    private val adapter: InstaReviewAdapter by lazy {
        InstaReviewAdapter(context) { reviewId ->
            val bundle = Bundle()
            bundle.putLong("reviewId", reviewId)
            bundle.putString("page", "review")
            findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail, bundle)
        }
    }

    var isTop = true


    private val viewModel: InstaReviewViewModel by viewModels( ownerProducer = { requireParentFragment()}, factoryProducer = { InstaReviewViewModelFactory() } )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentInstaReviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setToast()
        pagingRecyclerview()
        setFab()
        setReviewWriteBtn()
    }

    private fun setReviewWriteBtn() {
        with(binding) {
            instaReviewWriteButton.setOnClickListener {
                view?.let { it1 -> Navigation.findNavController(it1).navigate(R.id.action_reviewFragment_to_reviewAddSearch) }
            }

            instaReviewFab.setOnClickListener{

            }
        }
    }

    private fun setFab(){

        with(binding){

            instaReviewRecyclerView.addFabOnScrollListener(
                onHide = {
                    binding.instaReviewFab.visibility = View.GONE
                },
                onShow = {
                    binding.instaReviewFab.visibility = View.VISIBLE
                }
            )

        }
    }

    fun initView() {

        binding.apply {
            // 클릭 리스너 부분
            instaReviewRecyclerView.adapter = adapter
            val size = resources.getDimensionPixelSize(R.dimen.one_dp)
            val color = Color.WHITE
            val itemDecoration = GridItemDecoration(size, color)
            instaReviewRecyclerView.addItemDecoration(itemDecoration)
            instaReviewRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        }

        viewModel.instaReviews.observe(viewLifecycleOwner) {
            adapter.addItems(it)
        }
    }



fun pagingRecyclerview(){
    binding.instaReviewRecyclerView.addGridOnScrollEndListener {
        viewModel.onScrolled()
    }
    viewModel.scrollData.observe(viewLifecycleOwner){
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            adapter.addLoading()
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
