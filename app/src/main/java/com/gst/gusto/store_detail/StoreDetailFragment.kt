package com.gst.gusto.store_detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.StoreDetail
import com.gst.gusto.ListView.Model.StoreDetailReview
import com.gst.gusto.ListView.adapter.CategoryChooseBottomSheetDialog
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentStoreDetailBinding
import com.gst.gusto.store_detail.adapter.StoreDetailPhotoAdapter
import com.gst.gusto.store_detail.adapter.StoreDetailReviewAdapter

class StoreDetailFragment : Fragment() {

    private lateinit var binding : FragmentStoreDetailBinding
    private var sampleReviewDataArray = arrayListOf<StoreDetailReview>(
        StoreDetailReview(reviewId = 0, visitedAt = "2024-01-03", nickname = "귀여운 바질페스토 12", liked = 1, comment = "goooooood", hashTageName = arrayListOf("맛있음", "분위기"), date= "2024-01-04", photoArray = arrayListOf(R.drawable.sample_store_img, R.drawable.sample_store_2_img)),
        StoreDetailReview(reviewId = 1, visitedAt = "2024-01-02", nickname = "매콤한 통닭", liked = 3, comment = "맛있어요", hashTageName = arrayListOf("맛있음", "넓음"), date = "2024-01-02")
    )
    private var samplePhotoDataArray = arrayListOf<Int>(
        R.drawable.sample_store_img,
        R.drawable.sample_store_2_img,
        R.drawable.sample_store_3_img,
        R.drawable.sample_store_4_img
    )
    private var sampleData = StoreDetail(0, "Gusto Restaurant", "양식", "메롱시 메로나동 바밤바 24-6 1층", 1, 1, reviews = sampleReviewDataArray, reviewImg = arrayListOf(1, 2, 3))

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 데이터 적용
         */
        binding.ivStoreDetailBanner.setImageResource(R.drawable.sample_store_3_img)
        binding.tvStoreDetailCategory.text = sampleData.categoryName
        binding.tvStoreDetailName.text = sampleData.storeName
        binding.tvStoreDetailAddress.text = sampleData.address
        if(sampleData.pin == 1){
            binding.ivStoreDetailSave.setImageResource(R.drawable.save_o_img)
        }
        //리뷰사진 리사이클러뷰 연결
        val mStorePhotoAdapter = StoreDetailPhotoAdapter(samplePhotoDataArray)
        binding.rvStoreDetailPhoto.adapter = mStorePhotoAdapter
        //리뷰 리사이클러뷰 연결
        val mReviewAdapter = StoreDetailReviewAdapter()
        mReviewAdapter.setItemClickListener(object : StoreDetailReviewAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: StoreDetailReview) {
                //데이터 넣기
                Navigation.findNavController(view).navigate(R.id.action_storeDetailFragment_to_fragment_review_detail)
            }

        })
        mReviewAdapter.submitList(sampleReviewDataArray)
        binding.rvStoreDetailReview.adapter = mReviewAdapter
        binding.rvStoreDetailReview.layoutManager = LinearLayoutManager(this.requireActivity())


        /**
         * 뒤로가기 버튼 클릭 리스너
         */

        /**
         * tablayout 설정
         */

        /**
         * 찜 버튼 클릭 리스너
         */
        binding.ivStoreDetailSave.setOnClickListener {
            if(sampleData.pin == 1){
                binding.ivStoreDetailSave.setImageResource(R.drawable.save_x_img)
                // 서버 연결
                sampleData.pin = 0
            }
            else {
                //카테고리 선택 팝업창 노출
                val mChooseBottomSheetDialog = CategoryChooseBottomSheetDialog(){
                    when(it){
                        0 -> {
                            Log.d("bottomsheet", "카테고리 선택 click")
                            binding.ivStoreDetailSave.setImageResource(R.drawable.save_o_img)
                            sampleData.pin = 1
                        }
                    }
                }
                mChooseBottomSheetDialog.show(requireFragmentManager(), mChooseBottomSheetDialog.tag)
            }

        }
        /**
         * 더보기 클릭 시 페이징 처리
         */

        /**
         * 리뷰 추가 버튼 클릭 리스너
         */
        binding.fabStoreDetailAdd.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_storeDetailFragment_to_fragment_review_add_1)
        }


        /**
         * 리뷰 페이징
         */

    }
}