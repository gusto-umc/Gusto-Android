package com.gst.gusto.store_detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.NavigationMenu
import com.gst.gusto.ListView.Model.StoreDetail
import com.gst.gusto.ListView.Model.StoreDetailReview
import com.gst.gusto.ListView.adapter.CategoryChooseBottomSheetDialog
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseReviews
import com.gst.gusto.api.ResponseStoreDetail
import com.gst.gusto.databinding.FragmentStoreDetailBinding
import com.gst.gusto.store_detail.adapter.StoreDetailPhotoAdapter
import com.gst.gusto.store_detail.adapter.StoreDetailReviewAdapter

class StoreDetailFragment : Fragment() {

    private lateinit var binding : FragmentStoreDetailBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_detail, container, false)
        return binding.root
    }
    var pinId : Int? = null
    val mReviewAdapter = StoreDetailReviewAdapter()
    fun setDatas(data : ResponseStoreDetail?){
        if(data != null){
            binding.tvStoreDetailCategory.text = data!!.categoryString
            binding.tvStoreDetailName.text = data!!.storeName
            binding.tvStoreDetailAddress.text = data!!.address
            pinId = data!!.pinId
            if (data.pin){
                binding.ivStoreDetailSave.setImageResource(R.drawable.save_o_img)
            }
            //리뷰사진 리사이클러뷰 연결
            val mStorePhotos = ArrayList<String>().apply {
                for(i in data.reviewImg4){
                    this.add(i)
                }
            }
            val mStorePhotoAdapter = StoreDetailPhotoAdapter(mStorePhotos)
            mStorePhotoAdapter.mContext = context
            binding.rvStoreDetailPhoto.adapter = mStorePhotoAdapter
            //배너 사진 연결
            if(!data.reviewImg4.isNullOrEmpty()) {
                setImage(binding.ivStoreDetailBanner,data.reviewImg4[0], requireContext())
            }
            else{
                binding.ivStoreDetailBanner.setImageResource(R.drawable.gst_dummypic)
            }
            //리뷰 리사이클러뷰 연결
            mReviewAdapter.mContext = context
            mReviewAdapter.setItemClickListener(object : StoreDetailReviewAdapter.OnItemClickListener{
                override fun onClick(v: View, dataSet: ResponseReviews) {
                    //데이터 넣기
                    val bundle = Bundle()
                    bundle.putLong("reviewId",dataSet.reviewId)
                    bundle.putString("reviewNickname", dataSet.nickname)//리뷰 아이디 넘겨 주면 됨
                    if(dataSet.nickname == gustoViewModel.userNickname){
                        // 내 리뷰인 경우
                        Navigation.findNavController(view!!).navigate(R.id.action_storeDetailFragment_to_fragment_review_detail, bundle)
                    }
                    else{
                        //타 유저 리뷰인 경우
                        gustoViewModel.currentFeedReviewId = dataSet.reviewId
                        Log.d("feedId checking", "dataSet : ${dataSet.reviewId}, currentFeedReviewId : ${gustoViewModel.currentFeedReviewId}")
                        gustoViewModel.getFeedReview{ result ->
                            when(result) {
                                1 -> {
                                    findNavController().navigate(R.id.action_storeDetailFragment_to_fragment_feed_review_detail)
                                }
                            }
                        }
                    }

                }

            })
            mReviewAdapter.submitList(gustoViewModel.storeDetailReviews)
            binding.rvStoreDetailReview.adapter = mReviewAdapter
            binding.rvStoreDetailReview.layoutManager = LinearLayoutManager(this.requireActivity())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var sampleStoreId = gustoViewModel.selectedDetailStoreId

        gustoViewModel.detailReviewLastId = null
        gustoViewModel.detailReviewLastVisitedAt = null
        gustoViewModel.storeDetailReviews.clear()
        Log.d("reviewId check enter", gustoViewModel.detailReviewLastId.toString())

        /**
         * 데이터 적용
         */




        fun loadReviews(reviews : ArrayList<ResponseReviews>){
            mReviewAdapter.submitList(gustoViewModel.storeDetailReviews)
            binding.rvStoreDetailReview.adapter = mReviewAdapter
            binding.rvStoreDetailReview.layoutManager = LinearLayoutManager(this.requireActivity())
        }

        setDatas(gustoViewModel.myStoreDetail)

        gustoViewModel.getStoreDetail(sampleStoreId.toLong()){
            result ->
            when(result){
                0 -> {
                    //success
                    setDatas(gustoViewModel.myStoreDetail)
                    Log.d("reviewsId", gustoViewModel.detailReviewLastId.toString())

                }
                1 -> {
                    //fail
                    Toast.makeText(context, "detail 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * 뒤로가기 버튼 클릭 리스너
         */
        binding.ivStoreDetailBack.setOnClickListener {
            findNavController().popBackStack()
        }

        /**
         * tablayout 설정
         */

        /**
         * 찜 버튼 클릭 리스너
         */
        binding.ivStoreDetailSave.setOnClickListener {
            if(gustoViewModel.myStoreDetail!!.pin){
                gustoViewModel.deletePin(gustoViewModel.myStoreDetail!!.pinId){
                    result ->
                    when(result){
                        0-> {
                            //성공
                            binding.ivStoreDetailSave.setImageResource(R.drawable.save_x_img)
                            gustoViewModel.myStoreDetail!!.pin = false
                        }
                        1 -> {
                            //실패
                            Toast.makeText(context, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else {
                //카테고리 선택 팝업창 노출
                val mChooseBottomSheetDialog = CategoryChooseBottomSheetDialog(null){
                    result, rData ->
                    when(result){
                        1 -> {
                            Log.d("bottomsheet", "카테고리 선택 click")
                            binding.ivStoreDetailSave.setImageResource(R.drawable.save_o_img)
                            gustoViewModel.myStoreDetail!!.pin = true
                            gustoViewModel.myStoreDetail!!.pinId = rData!!.pinId
                        }
                    }
                }
                mChooseBottomSheetDialog.viewModel = gustoViewModel
                mChooseBottomSheetDialog.show(requireFragmentManager(), mChooseBottomSheetDialog.tag)
            }

        }

        /**
         * 리뷰 추가 버튼 클릭 리스너
         */
        binding.fabStoreDetailAdd.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_storeDetailFragment_to_fragment_review_add_1)
        }


        /**
         * 리뷰 페이징 더보기
         */
        binding.tvReviewLoad.setOnClickListener {
            gustoViewModel.getStoreDetail(sampleStoreId.toLong()){
                    result ->
                when(result){
                    0 -> {
                        //success
                        Log.d("reviews more load", gustoViewModel.myStoreDetail!!.reviews.toString())
                        Log.d("reviews more load", gustoViewModel.detailReviewLastId.toString())
                        loadReviews(gustoViewModel.storeDetailReviews)
                        if(gustoViewModel.myStoreDetail!!.reviews.isEmpty()){
                            binding.tvReviewLoad.visibility = View.INVISIBLE
                        }
                    }
                    1 -> {
                        //fail
                        Toast.makeText(context, "detail 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        /**
         * search route 테스트
         */
        binding.tvStoreDetailName.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_storeDetailFragment_to_routeSearchFragment)
        }



        /**
         * map icon 클릭리스너
         */
        binding.ivStoreDetailMap.setOnClickListener {
            gustoViewModel.selectStoreId = gustoViewModel.selectedDetailStoreId.toLong()
            gustoViewModel.storeIdList.clear()
            gustoViewModel.storeIdList.add(gustoViewModel.selectStoreId)
            Navigation.findNavController(view).navigate(R.id.action_storeDetailFragment_to_fragment_map_viewpager)
        }


    }

    override fun onResume() {
        super.onResume()
        setDatas(gustoViewModel.myStoreDetail)
    }
}