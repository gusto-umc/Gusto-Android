package com.gst.clock.Fragment

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.util.util.Companion.dpToPixels
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewDetail2Binding
import com.gst.gusto.databinding.FragmentReviewDetailBinding
import com.gst.gusto.review_write.adapter.ImageViewPagerAdapter
import com.gst.gusto.review_write.adapter.ReviewHashTagAdapter
import com.gst.gusto.util.util
import com.gst.gusto.util.util.Companion.setImage
import java.time.LocalDate

class ReviewDetailFragment : Fragment() {

    lateinit var binding: FragmentReviewDetail2Binding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var page : String
    private lateinit var activity : MainActivity



    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity = requireActivity() as MainActivity
                activity.hideBottomNavigation(false)
                findNavController().popBackStack()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as MainActivity
        activity.hideBottomNavigation(true)


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewDetail2Binding.inflate(inflater, container, false)



        //1. argument 저장
        var reviewId = arguments?.getLong("reviewId")
        Log.d("review detail check", reviewId.toString())
        gustoViewModel.myReviewId = reviewId

        //2. 서버 연결
        gustoViewModel.getReview(gustoViewModel.myReviewId!!){
            result ->
            when(result){
                0 -> {
                    //success
                    //데이터 적용 - 날짜
                    val reviewDate = LocalDate.parse(gustoViewModel.myReview!!.visitedAt)
                    binding.tvDay1.text = "${reviewDate.monthValue}월 ${reviewDate.dayOfMonth}일"
                    binding.tvDay2.text = "${reviewDate.year} ${reviewDate.monthValue} ${reviewDate.dayOfMonth} 방문"
                    //데이터 적용 - 가게명
                    binding.tvReviewStoreName.text = gustoViewModel.myReview!!.storeName
                    binding.tvReviewStoreName.setOnClickListener {
                        gustoViewModel.selectedDetailStoreId = gustoViewModel.myReview!!.storeId.toInt()
                        findNavController().navigate(R.id.action_reviewDetail_to_storeDetailFragment)
                    }
                    //데이터 적용 - 해시태그 rv
                    val flexboxLayoutManager = FlexboxLayoutManager(context)
                    flexboxLayoutManager.justifyContent = JustifyContent.FLEX_START
                    val hashtagAdapter = gustoViewModel.myReview!!.hashTags?.let {
                        ReviewHashTagAdapter(
                            it
                        )
                    }
                    hashtagAdapter?.gustoViewModel = gustoViewModel
                    binding.flexboxRv.adapter = hashtagAdapter
                    binding.flexboxRv.layoutManager = flexboxLayoutManager

                    //데이터 적용 - 하트수
                    binding.tvHeartNum.text = gustoViewModel.myReview!!.likeCnt.toString()
                    //데이터 적용 - 메뉴
                    binding.menuTextTv.text = if(gustoViewModel.myReview!!.menuName.isNullOrBlank()){
                        ""
                    } else{
                        gustoViewModel.myReview!!.menuName
                    }
                    //데이터 적용 - lock
                    if(gustoViewModel.myReview!!.publicCheck){
                        binding.ivReviewLock.visibility = View.INVISIBLE
                    }else{
                        binding.ivReviewLock.visibility = View.VISIBLE
                    }
                    //taste 처리
                    //init rating bar
                    binding.reviewRate1.visibility = View.INVISIBLE
                    binding.reviewRate2.visibility = View.INVISIBLE
                    binding.reviewRate3.visibility = View.INVISIBLE
                    binding.reviewRate4.visibility = View.INVISIBLE
                    binding.reviewRate5.visibility = View.INVISIBLE

                    when(gustoViewModel.myReview!!.taste){
                        0 -> {}
                        1 -> {
                            binding.reviewRate1.visibility = View.VISIBLE
                        }
                        2 -> {
                            binding.reviewRate1.visibility = View.VISIBLE
                            binding.reviewRate2.visibility = View.VISIBLE
                        }
                        3 -> {
                            binding.reviewRate1.visibility = View.VISIBLE
                            binding.reviewRate2.visibility = View.VISIBLE
                            binding.reviewRate3.visibility = View.VISIBLE

                        }
                        4 -> {
                            binding.reviewRate1.visibility = View.VISIBLE
                            binding.reviewRate2.visibility = View.VISIBLE
                            binding.reviewRate3.visibility = View.VISIBLE
                            binding.reviewRate4.visibility = View.VISIBLE
                        }
                        5 -> {
                            binding.reviewRate1.visibility = View.VISIBLE
                            binding.reviewRate2.visibility = View.VISIBLE
                            binding.reviewRate3.visibility = View.VISIBLE
                            binding.reviewRate4.visibility = View.VISIBLE
                            binding.reviewRate5.visibility = View.VISIBLE
                        }
                        else -> {}
                    }
                    //comment 처리
                    binding.reviewTextTv.text = if(gustoViewModel.myReview!!.comment == null){
                        ""
                    } else{
                        gustoViewModel.myReview!!.comment
                    }
                    gustoViewModel.changeReviewFlag(true)

                }
                1 -> {
                    //fail
                    Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
        binding.btnHeart.setOnClickListener {
            Toast.makeText(context, "본인의 리뷰에는 좋아요를 누를 수 없습니다.", Toast.LENGTH_SHORT).show()
        }



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /**
         * 서버 데이터 연결
         */
        gustoViewModel.successFlg.observe(viewLifecycleOwner, Observer{
            if(it){
                setImage(binding.ivReviewImg, gustoViewModel.myReview?.img!!.first(), requireContext() )
            }

        })



        /**
         * setting 클릭 리스너
         */

        binding.ivReviewEdit.setOnClickListener {
            findNavController().navigate(R.id.action_reviewDetail_to_reviewDetailEdit)
            activity.hideBottomNavigation(false)

        }
        //리뷰 삭제
        binding.ivReviewDelete.setOnClickListener {
            //util.setPopupTwo(context, "${gustoViewModel.myReview.storeName}의 리뷰를 정말로 삭제하시겠습니까?")
            util.setPopupTwo(requireContext(), "${gustoViewModel.myReview!!.storeName}의 리뷰를 정말로 삭제하시겠습니까?", "", 2){
                    result ->
                when(result){
                    0 -> {
                        //yes : remove
                        gustoViewModel.deleteReview(reviewId = gustoViewModel.myReviewId!!){
                                result ->
                            when(result){
                                0 -> {
                                    //성공
                                    Navigation.findNavController(view).popBackStack()

                                }
                                1 -> {
                                    //실페
                                    Toast.makeText(context, "리뷰 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    }
                    1 -> {
                        //no

                    }
                }
            }

        }
        activity.setTrans(false)
    }

    override fun onResume() {
        super.onResume()
        activity = requireActivity() as MainActivity
        activity.hideBottomNavigation(true)
        activity.setTrans(true)
    }

    override fun onStop() {
        super.onStop()
        activity.setTrans(false)
    }





}