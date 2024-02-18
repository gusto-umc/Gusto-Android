package com.gst.gusto.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentFeedSearchBinding
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.gst.gusto.MainActivity
import com.gst.gusto.Util.util
import com.gst.gusto.api.GustoViewModel

class FeedSearchFragment() : Fragment() {

    lateinit var binding: FragmentFeedSearchBinding

    var clickList: ArrayList<Boolean> = arrayListOf(true, true, true, true, true, true, true, true)

    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedSearchBinding.inflate(inflater, container, false)

        cancel()
        hashClick()
        searchKeyWord()

        return binding.root
    }

    // 해시태그 버튼 클릭리스너
    fun hashTagClick(hashTag: TextView, index: Int) {
        hashTag.apply {
            setOnClickListener {
                if (clickList[index]) {
                    background = ContextCompat.getDrawable(context, R.drawable.background_radius_feed_search_on)
                    clickList[index] = false
                } else {
                    background = ContextCompat.getDrawable(context, R.drawable.background_radius_feed_search_off)
                    clickList[index] = true
                }
            }
        }
    }

    // FeedFragment로 이동
    fun moveFeed(){
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    fun cancel(){
        binding.apply {

            util.showSoftInputFragment(feedSearch, activity)

            // 바깥 부분 클릭
            linearLayout.setOnClickListener {
                moveFeed()
            }

            // 뒤로가기
            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    moveFeed()
                }
            })

        }
    }

    fun hashClick(){
        binding.apply{
            // 해시태그 버튼들 클릭
            hashTagClick(warm, 0)
            hashTagClick(clean, 1)
            hashTagClick(insta, 2)
            hashTagClick(comportable, 3)
            hashTagClick(cute, 4)
            hashTagClick(wide, 5)
            hashTagClick(mood, 6)
            hashTagClick(cost, 7)
        }
    }

    fun searchKeyWord(){
        var testHashList = listOf(1L, 2L, 3L)
        with(binding) {
            feedSearch.setOnTouchListener(View.OnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (feedSearch.right - feedSearch.compoundDrawables[2].bounds.width())) {
                        getData(feedSearch.text.toString(), testHashList)
                        moveFeed()
                        return@OnTouchListener true
                    }
                }
                false
            })
        }
    }

    fun getData(keyword: String, hashTags: List<Long>) {
        gustoViewModel.getTokens(requireActivity() as MainActivity)
        gustoViewModel.feedSearch(keyword, hashTags) { result, response ->
            if (result == 1) {
                if (response != null) {
                    gustoViewModel.searchFeedData?.value = response
                }
                Log.d("searchResponse", gustoViewModel.searchFeedData.toString())
            }
        }
    }

}



