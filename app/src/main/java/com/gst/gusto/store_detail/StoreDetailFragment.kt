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
    private var sampleReviewDataArray = arrayListOf<StoreDetailReview>(
        StoreDetailReview(reviewId = 0, visitedAt = "2024-01-03", nickname = "귀여운 바질페스토 12", liked = 1, comment = "goooooood", hashTageName = arrayListOf("맛있음", "분위기"), date= "2024-01-04", photoArray = arrayListOf(R.drawable.sample_store_img, R.drawable.sample_store_2_img)),
        StoreDetailReview(reviewId = 1, visitedAt = "2024-01-02", nickname = "매콤한 통닭", liked = 3, comment = "맛있어요", hashTageName = arrayListOf("맛있음", "넓음"), date = "2024-01-02", photoArray = arrayListOf(R.drawable.sample_store_img, R.drawable.sample_store_2_img))
    )
    private var samplePhotoDataArray = arrayListOf<Int>(
        R.drawable.sample_store_img,
        R.drawable.sample_store_2_img,
        R.drawable.sample_store_3_img,
        R.drawable.sample_store_4_img
    )
    private var sampleData = StoreDetail(0, "Gusto Restaurant", "양식", "메롱시 메로나동 바밤바 24-6 1층", 1, 0, reviews = sampleReviewDataArray, reviewImg = arrayListOf(1, 2, 3))

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
        var sampleStoreId = 1
        gustoViewModel.detailReviewLastId = null
        gustoViewModel.storeDetailReviews.clear()
        Log.d("reviewId check enter", gustoViewModel.detailReviewLastId.toString())

        /**
         * 데이터 적용
         */
        val mReviewAdapter = StoreDetailReviewAdapter()

        fun setDatas(data : ResponseStoreDetail?){
            if(data == null){
                binding.ivStoreDetailBanner.setImageResource(R.drawable.sample_store_3_img)
                binding.tvStoreDetailCategory.text = sampleData.categoryName
                binding.tvStoreDetailName.text = sampleData.storeName
                binding.tvStoreDetailAddress.text = sampleData.address
                if(sampleData.pin == 1){
                    binding.ivStoreDetailSave.setImageResource(R.drawable.save_o_img)
                }
                Log.d("review checking", gustoViewModel.detailReviewLastId.toString())
            }
            else{
                binding.tvStoreDetailCategory.text = data!!.categoryName
                binding.tvStoreDetailName.text = data!!.storeName
                binding.tvStoreDetailAddress.text = data!!.address
                if (data.pin){
                    binding.ivStoreDetailSave.setImageResource(R.drawable.save_o_img)
                }
                //리뷰사진 리사이클러뷰 연결
                val mStorePhotos = ArrayList<String>().apply {
                    add(data.reviewImg4[1])
                    add(data.reviewImg4[2])
                    add(data.reviewImg4[3])
                }
                val mStorePhotoAdapter = StoreDetailPhotoAdapter(mStorePhotos)
                mStorePhotoAdapter.mContext = context
                binding.rvStoreDetailPhoto.adapter = mStorePhotoAdapter
                //배너 사진 연결
                setImage(binding.ivStoreDetailBanner,data.reviewImg4[0], requireContext())
                //리뷰 리사이클러뷰 연결
                mReviewAdapter.mContext = context
                mReviewAdapter.setItemClickListener(object : StoreDetailReviewAdapter.OnItemClickListener{
                    override fun onClick(v: View, dataSet: ResponseReviews) {
                        //데이터 넣기
                        val bundle = Bundle()
                        bundle.putInt("reviewId",dataSet.reviewId)
                        bundle.putString("reviewNickname", dataSet.nickname)//리뷰 아이디 넘겨 주면 됨
                        if(dataSet.nickname == gustoViewModel.userNickname){
                            // 내 리뷰인 경우
                            Navigation.findNavController(view).navigate(R.id.action_storeDetailFragment_to_fragment_review_detail, bundle)
                        }
                        else{
                            //타 유저 리뷰인 경우
                            Navigation.findNavController(view).navigate(R.id.action_storeDetailFragment_to_fragment_feed_review_detail,bundle)
                        }

                    }

                })
                mReviewAdapter.submitList(gustoViewModel.storeDetailReviews)
                binding.rvStoreDetailReview.adapter = mReviewAdapter
                binding.rvStoreDetailReview.layoutManager = LinearLayoutManager(this.requireActivity())
            }

        }

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
                    Toast.makeText(context, "detail 성공", Toast.LENGTH_SHORT).show()
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
            if(sampleData.pin == 1){
                gustoViewModel.deletePin(sampleStoreId){
                    result ->
                    when(result){
                        0-> {
                            //성공
                            binding.ivStoreDetailSave.setImageResource(R.drawable.save_x_img)
                            sampleData.pin = 0
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
                val mChooseBottomSheetDialog = CategoryChooseBottomSheetDialog(){
                    when(it){
                        1 -> {
                            Log.d("bottomsheet", "카테고리 선택 click")
                            binding.ivStoreDetailSave.setImageResource(R.drawable.save_o_img)
                            sampleData.pin = 1
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
         * 리뷰 페이징 test
         */
        binding.tvReviewLoad.setOnClickListener {
            gustoViewModel.getStoreDetail(sampleStoreId.toLong()){
                    result ->
                when(result){
                    0 -> {
                        //success
                        Toast.makeText(context, "detail 성공", Toast.LENGTH_SHORT).show()
                        Log.d("reviewsId", gustoViewModel.myStoreDetail!!.reviews.toString())
                        loadReviews(gustoViewModel.storeDetailReviews)
                    }
                    1 -> {
                        //fail
                        Toast.makeText(context, "detail 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }



        /**
         * map icon 클릭리스너
         */


    }
}