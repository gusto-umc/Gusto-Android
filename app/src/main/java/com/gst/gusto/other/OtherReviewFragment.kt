package com.gst.gusto.other

import android.graphics.Color
import android.os.Bundle
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
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMyReviewBinding
import com.gst.gusto.other.viewmodel.OtherReviewViewModel
import com.gst.gusto.other.viewmodel.OtherReviewViewModelFactory
import com.gst.gusto.review.adapter.InstaReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration
import com.gst.gusto.util.ScrollUtil.addGridOnScrollEndListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtherReviewFragment : Fragment() {

    lateinit var binding: FragmentMyReviewBinding

    private val gustoViewModel : GustoViewModel by activityViewModels()

    private val otherReviewViewModel: OtherReviewViewModel by viewModels (ownerProducer = { requireActivity()}, factoryProducer = { OtherReviewViewModelFactory() })

    private val adapter: InstaReviewAdapter by lazy {
        InstaReviewAdapter(context) { reviewId ->
            gustoViewModel.currentFeedReviewId = reviewId
            gustoViewModel.getFeedReview{ result ->
                when(result) {
                    1 -> {
                        findNavController().navigate(R.id.action_otherFragment_to_feedDetail)
                    }
                }
            }
        }
    }

    private val itemDecoration : GridItemDecoration by lazy {
        GridItemDecoration(resources.getDimensionPixelSize(R.dimen.one_dp), Color.WHITE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyReviewBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            otherReviewViewModel.getOtherInstaReview(gustoViewModel.currentFeedNickname, 12)
        }

        initView()
        setToast()
        pagingRecyclerview()
    }

    fun initView(){
        binding.apply {
            // 클릭 리스너 부분
            recyclerView.adapter = adapter
            if(itemDecoration != null){
                recyclerView.removeItemDecoration(itemDecoration)
            }
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
        }

        otherReviewViewModel.instaReviews.observe(viewLifecycleOwner) {
            adapter.addItems(it)
        }
    }

    fun pagingRecyclerview(){
        binding.recyclerView.addGridOnScrollEndListener {
            otherReviewViewModel.onScrolled(gustoViewModel.currentFeedNickname)
        }
        otherReviewViewModel.scrollData.observe(viewLifecycleOwner){
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000)
                adapter.removeLoading()
            }
        }
    }

    fun setToast(){
        otherReviewViewModel.tokenToastData.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), "토큰을 재 발급 중입니다", Toast.LENGTH_SHORT).show()
        }
        otherReviewViewModel.errorToastData.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
        }
    }

}