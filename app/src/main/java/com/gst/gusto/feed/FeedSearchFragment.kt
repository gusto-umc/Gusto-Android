package com.gst.gusto.feed

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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

    var hashClickList: ArrayList<Boolean> = arrayListOf(true, true, true, true, true, true, true, true, true, true, true)
    var hashSearchList: ArrayList<Long>? = ArrayList()

    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedSearchBinding.inflate(inflater, container, false)

        cancel()
        searchKeyWord()

        return binding.root
    }

    // 해시태그 버튼 클릭리스너
    fun hashTagClick(hashTag: TextView, index: Int) {
        hashTag.apply {
            setOnClickListener {
                if (hashClickList[index]) {
                    background = ContextCompat.getDrawable(context, R.drawable.background_radius_feed_search_on)
                } else {
                    background = ContextCompat.getDrawable(context, R.drawable.background_radius_feed_search_off)
                }
                hashClickList[index] = !hashClickList[index]
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
            hashTagClick(restroom, 1)
            hashTagClick(comportable, 2)
            hashTagClick(cute, 3)
            hashTagClick(clean, 4)
            hashTagClick(insta, 5)
            hashTagClick(hip, 6)
            hashTagClick(okay, 7)
            hashTagClick(wide, 8)
            hashTagClick(mood, 9)
            hashTagClick(cost, 10)
        }
    }

    fun searchKeyWord(){

        hashClick()

        with(binding) {

            feedSearch.setOnEditorActionListener{ textView, action, event ->
                var handled = false

                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    hashSearchList?.clear()
                    for(hashClick in 1.. hashClickList.size ){
                        Log.d("Search", "${hashClick}은 ${hashClickList[hashClick - 1]}")
                        if(!hashClickList[hashClick - 1]){
                            hashSearchList?.add(hashClick.toLong())
                        }
                    }

                    val tags = hashSearchList?.toList() ?: emptyList()
                    getData(feedSearch.text.toString(), tags)
                    moveFeed()
                    handled = true
                }

                handled
            }

            val delegateArea = Rect()
            feedSearch.post {
                feedSearch.getHitRect(delegateArea)
                delegateArea.right += 200  // 오른쪽 패딩을 늘립니다.

                val touchDelegate = TouchDelegate(delegateArea, feedSearch)
                if (View::class.java.isInstance(feedSearch.parent)) {
                    (feedSearch.parent as View).touchDelegate = touchDelegate
                }
            }

            feedSearch.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val touchableArea = feedSearch.right - feedSearch.compoundDrawables[2].bounds.width() - 50  // 50 픽셀만큼 더 넓게 설정
                    if (event.rawX >= touchableArea) {
                        // 검색을 실행하기 전에 해시태그 리스트를 초기화하고, 선택되지 않은 해시태그들만 다시 추가합니다.
                        hashSearchList?.clear()
                        for(hashClick in 1.. hashClickList.size ){
                            Log.d("Search", "${hashClick}은 ${hashClickList[hashClick - 1]}")
                            if(!hashClickList[hashClick - 1]){
                                hashSearchList?.add(hashClick.toLong())
                            }
                        }

                        val tags = hashSearchList?.toList() ?: emptyList()

                        // 검색을 실행합니다.
                        getData(feedSearch.text.toString(), tags)
                        moveFeed()
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }
    }

    fun getData(keyword: String, hashTags: List<Long>?) {
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



