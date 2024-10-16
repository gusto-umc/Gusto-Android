package com.gst.clock.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentFeedDetailBinding
import com.gst.gusto.review_write.adapter.ReviewHashTagAdapter

class FeedDetailReviewFragment : Fragment() {

    lateinit var binding: FragmentFeedDetailBinding
    private lateinit var scaleUpAnimation: ScaleAnimation
    private lateinit var scaleDownAnimation: ScaleAnimation
    private lateinit var bounceInterpolator: BounceInterpolator
    private val gustoViewModel : GustoViewModel by activityViewModels()
    lateinit var page : String
    private var likeIt = 0
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
        binding = FragmentFeedDetailBinding.inflate(inflater, container, false)



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 데이터 세팅
        val feedDetail = gustoViewModel.currentFeedData
        var feedId = arguments?.getLong("reviewId")

        gustoViewModel.currentFeedNickname = feedDetail.nickName

        /**
         * storename, menu, comment
         */
        binding.tvFeedStoreName.text = feedDetail.storeName
        binding.tvFeedStoreName.setOnClickListener {
            gustoViewModel.selectedDetailStoreId = gustoViewModel.currentFeedData.storeId.toInt()
            findNavController().navigate(R.id.action_feedDetailReview_to_storeDetailFragment)
            activity.hideBottomNavigation(false)
        }
        binding.feedMenuTextTv.text = feedDetail.menuName
        binding.feedReviewTextTv.text = feedDetail.comment
        binding.tvFeedDetailUser.text = feedDetail.nickName

        /**
         *  review img, user img, taste, hashtag
         */

        //img 적용
        var photoPosition = 0
        fun changePhoto(size : Int, position : Int){
            if(position == (size -1)){
                photoPosition = 0
                setImage(binding.ivFeedImg, feedDetail.images!!.first(), requireContext())
                when(size){
                    2 -> {
                        binding.viewLayout21.setBackgroundResource(R.color.white)
                        binding.viewLayout21.setBackgroundResource(R.color.gray_navi)
                    }
                    3 -> {
                        binding.viewLayout30.setBackgroundResource(R.color.white)
                        binding.viewLayout31.setBackgroundResource(R.color.gray_navi)
                        binding.viewLayout32.setBackgroundResource(R.color.gray_navi)

                    }
                    4 -> {
                        binding.viewLayout40.setBackgroundResource(R.color.white)
                        binding.viewLayout41.setBackgroundResource(R.color.gray_navi)
                        binding.viewLayout42.setBackgroundResource(R.color.gray_navi)
                        binding.viewLayout43.setBackgroundResource(R.color.gray_navi)
                    }
                }
            }else{
                photoPosition++
                setImage(binding.ivFeedImg, feedDetail.images!![photoPosition], requireContext())
                when(size){
                    2 -> {
                        binding.viewLayout21.setBackgroundResource(R.color.white)
                    }
                    3 -> {
                        when(photoPosition){
                            1 -> {binding.viewLayout31.setBackgroundResource(R.color.white)}
                            2 -> {binding.viewLayout32.setBackgroundResource(R.color.white)}
                        }
                    }
                    4 -> {
                        when(photoPosition){
                            1 -> {binding.viewLayout41.setBackgroundResource(R.color.white)}
                            2 -> {binding.viewLayout42.setBackgroundResource(R.color.white)}
                            3 -> {binding.viewLayout43.setBackgroundResource(R.color.white)}
                        }
                    }
                }

            }

        }
        setImage(binding.ivFeedImg, feedDetail.images!!.first(), requireContext() )
        binding.layoutOtherUser.setOnClickListener {
            if(gustoViewModel.currentFeedNickname!=""){
                findNavController().navigate(R.id.action_feedDetailReview_to_otherFragment)
            }
        }
        setImage(binding.ivUserImgFeedDetail, feedDetail.profileImage, requireContext())

        when(feedDetail.images!!.size){
            1 -> {
                //visibility 처리
                binding.feedDetailBarLayout1.visibility = View.VISIBLE
                binding.feedDetailBarLayout2.visibility = View.INVISIBLE
                binding.feedDetailBarLayout3.visibility = View.INVISIBLE
                binding.feedDetailBarLayout4.visibility = View.INVISIBLE

                //image 적용
                setImage(binding.ivFeedImg, feedDetail.images!!.first(), requireContext() )
                photoPosition = 0
            }
            2 -> {
                //visibility 처리
                binding.feedDetailBarLayout1.visibility = View.INVISIBLE
                binding.feedDetailBarLayout2.visibility = View.VISIBLE
                binding.feedDetailBarLayout3.visibility = View.INVISIBLE
                binding.feedDetailBarLayout4.visibility = View.INVISIBLE

                setImage(binding.ivFeedImg, feedDetail.images!!.first(), requireContext() )
                photoPosition = 0

                binding.viewLayout21.setBackgroundResource(R.color.white)
                binding.viewLayout21.setBackgroundResource(R.color.gray_navi)

            }
            3 -> {
                //visibility 처리
                binding.feedDetailBarLayout1.visibility = View.INVISIBLE
                binding.feedDetailBarLayout2.visibility = View.INVISIBLE
                binding.feedDetailBarLayout3.visibility = View.VISIBLE
                binding.feedDetailBarLayout4.visibility = View.INVISIBLE

                setImage(binding.ivFeedImg, feedDetail.images!!.first(), requireContext() )
                photoPosition = 0

                binding.viewLayout30.setBackgroundResource(R.color.white)
                binding.viewLayout31.setBackgroundResource(R.color.gray_navi)
                binding.viewLayout32.setBackgroundResource(R.color.gray_navi)
            }
            4 -> {
                //visibility 처리
                binding.feedDetailBarLayout1.visibility = View.INVISIBLE
                binding.feedDetailBarLayout2.visibility = View.INVISIBLE
                binding.feedDetailBarLayout3.visibility = View.INVISIBLE
                binding.feedDetailBarLayout4.visibility = View.VISIBLE

                setImage(binding.ivFeedImg, feedDetail.images!!.first(), requireContext() )
                photoPosition = 0

                binding.viewLayout40.setBackgroundResource(R.color.white)
                binding.viewLayout41.setBackgroundResource(R.color.gray_navi)
                binding.viewLayout42.setBackgroundResource(R.color.gray_navi)
                binding.viewLayout43.setBackgroundResource(R.color.gray_navi)
            }
            else -> findNavController().popBackStack()
        }
        binding.ivFeedImg.setOnClickListener {
            if(feedDetail.images!!.size >= 2){
                changePhoto(feedDetail.images!!.size, photoPosition)
            }
        }



        //taste 처리
        binding.feedRate1.visibility = View.INVISIBLE
        binding.feedRate2.visibility = View.INVISIBLE
        binding.feedRate3.visibility = View.INVISIBLE
        binding.feedRate4.visibility = View.INVISIBLE
        binding.feedRate5.visibility = View.INVISIBLE

        when(feedDetail.taste){
            0 -> {}
            1 -> {
                binding.feedRate1.visibility = View.VISIBLE
            }
            2 -> {
                binding.feedRate1.visibility = View.VISIBLE
                binding.feedRate2.visibility = View.VISIBLE
            }
            3 -> {
                binding.feedRate1.visibility = View.VISIBLE
                binding.feedRate2.visibility = View.VISIBLE
                binding.feedRate3.visibility = View.VISIBLE

            }
            4 -> {
                binding.feedRate1.visibility = View.VISIBLE
                binding.feedRate2.visibility = View.VISIBLE
                binding.feedRate3.visibility = View.VISIBLE
                binding.feedRate4.visibility = View.VISIBLE
            }
            5 -> {
                binding.feedRate1.visibility = View.VISIBLE
                binding.feedRate2.visibility = View.VISIBLE
                binding.feedRate3.visibility = View.VISIBLE
                binding.feedRate4.visibility = View.VISIBLE
                binding.feedRate5.visibility = View.VISIBLE
            }
            else -> {}
        }

        //hashtag 처리
        val flexboxLayoutManager = FlexboxLayoutManager(context)
        flexboxLayoutManager.justifyContent = JustifyContent.FLEX_START
        val hashtagAdapter = feedDetail.hashTags?.let {
            ReviewHashTagAdapter(
                it
            )
        }
        hashtagAdapter?.gustoViewModel = gustoViewModel
        binding.feedFlexboxRv.adapter = hashtagAdapter
        binding.feedFlexboxRv.layoutManager = flexboxLayoutManager

        /**
         * heart 처리
         */
        // 하트 적용
        binding.tvFeedHeartNum.text = "${feedDetail.likeCnt}"
        if(feedDetail.likeCheck){
            val color = ContextCompat.getColor(requireContext(), R.color.main_C)
            binding.ivFeedHeart.setColorFilter(color)
        }
        else{
            binding.ivFeedHeart.setColorFilter(null)
        }


        // 하트 클릭 리스너
        bounceInterpolator = BounceInterpolator()

        // 확대 애니메이션
        scaleUpAnimation = ScaleAnimation(
            1.0f, 1.2f, 1.0f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleUpAnimation.duration = 500
        scaleUpAnimation.interpolator = bounceInterpolator

        // 축소 애니메이션
        scaleDownAnimation = ScaleAnimation(
            1.2f, 1.0f, 1.2f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleDownAnimation.duration = 500
        scaleDownAnimation.interpolator = bounceInterpolator

        binding.btnFeedHeart.setOnClickListener {
            if (it.isSelected) {
                // 좋아요 취소
                likeIt = 2
                binding.ivFeedHeart.setColorFilter(null)
                it.startAnimation(scaleDownAnimation)
                binding.tvFeedHeartNum.text = (binding.tvFeedHeartNum.text.toString().toInt()-1).toString()
            } else {
                // 좋아요
                likeIt = 1
                val color = ContextCompat.getColor(requireContext(), R.color.main_C)
                binding.ivFeedHeart.setColorFilter(color)
                it.startAnimation(scaleUpAnimation)
                binding.tvFeedHeartNum.text = (binding.tvFeedHeartNum.text.toString().toInt()+1).toString()
            }
            it.isSelected = !it.isSelected
        }
        if(feedDetail.likeCheck) {
            binding.btnFeedHeart.callOnClick()
            binding.tvFeedHeartNum.text = (binding.tvFeedHeartNum.text.toString().toInt()-1).toString()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if(likeIt==1) {
            gustoViewModel.lickReview{ result ->
                when(result) {
                    1 -> {

                    }
                }
            }
        } else if(likeIt==2) {
            gustoViewModel.unlickReview{ result ->
                when(result) {
                    1 -> {

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