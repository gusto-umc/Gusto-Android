package com.gst.gusto.review.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseListReview
import com.gst.gusto.databinding.FragmentListReviewBinding
import com.gst.gusto.model.TimeLineReview
import com.gst.gusto.review.adapter.ListReviewAdapter
import com.gst.gusto.review.adapter.ListReviewType
import com.gst.gusto.review.viewmodel.ListReviewViewModel
import com.gst.gusto.review.viewmodel.ListReviewViewModelFactory
import com.gst.gusto.util.ScrollUtil.addLinearOnScrollEndListener
import java.text.SimpleDateFormat
import java.util.Locale

class ListReviewFragment : Fragment() {

    lateinit var binding: FragmentListReviewBinding
    private val adapter: ListReviewAdapter by lazy{
        ListReviewAdapter(itemClickListener = {
            if(it.viewType == ListReviewType.LISTREVIEW){
                val bundle = Bundle()
                bundle.putLong("reviewId", it.reviewId)     //리뷰 아이디 넘겨 주면 됨
                bundle.putString("page","review")
                findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail,bundle)
            } else {
                // 리뷰 작성 버튼
            }

        })
    }

    private val gustoViewModel : GustoViewModel by activityViewModels()
    private val listReviewViewModel: ListReviewViewModel by viewModels(ownerProducer = { requireParentFragment() }, factoryProducer = { ListReviewViewModelFactory() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentListReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        pagingRecyclerview()
    }

    fun initView(){
        with(binding){
            recyclerView.adapter = adapter

            listReviewViewModel.timeLineReviews.observe(viewLifecycleOwner){
                adapter.addData(it)
            }
        }
    }

    fun pagingRecyclerview(){
        binding.recyclerView.addLinearOnScrollEndListener{
            listReviewViewModel.onScrolled()
        }
        listReviewViewModel.timeLineHasNext.observe(viewLifecycleOwner){
            if(it == false){
                listReviewViewModel.addLastData()
            }
        }
    }

    fun setToast(){
        listReviewViewModel.tokenToastData.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), "토큰을 재 발급 중입니다", Toast.LENGTH_SHORT).show()
        }
        listReviewViewModel.errorToastData.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
        }
    }

}