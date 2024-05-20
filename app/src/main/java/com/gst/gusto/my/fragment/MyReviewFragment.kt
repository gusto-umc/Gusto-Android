package com.gst.clock.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMyReviewBinding
import com.gst.gusto.my.viewmodel.MyReviewViewModel
import com.gst.gusto.my.viewmodel.MyReviewViewModelFactory
import com.gst.gusto.review.adapter.InstaReviewAdapter
import com.gst.gusto.review.adapter.GridItemDecoration
import com.gst.gusto.util.ScrollUtil.addOnScrollEndListener

class MyReviewFragment : Fragment() {

    lateinit var binding: FragmentMyReviewBinding

    private val adapter: InstaReviewAdapter by lazy {
        InstaReviewAdapter(context) { reviewId ->
            val bundle = Bundle()
            bundle.putLong("reviewId", reviewId)
            bundle.putString("page", "review")
            findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail, bundle)
        }
    }

    private val viewModel: MyReviewViewModel by viewModels( ownerProducer = { requireParentFragment() }, factoryProducer = { MyReviewViewModelFactory() } )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyReviewBinding.inflate(inflater, container, false)

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
            adapter.addItems(it)
        }
    }



    fun pagingRecyclerview(){
        binding.recyclerView.addOnScrollEndListener {
            viewModel.onScrolled()
        }
        viewModel.scrollData.observe(viewLifecycleOwner){
            adapter.removeLoading()
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