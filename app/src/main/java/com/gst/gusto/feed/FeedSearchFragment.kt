package com.gst.gusto.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentFeedSearchBinding
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.gst.gusto.Util.util

class FeedSearchFragment : Fragment() {

    lateinit var binding: FragmentFeedSearchBinding

    var clickList: ArrayList<Boolean> = arrayListOf(true, true, true, true, true, true, true, true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedSearchBinding.inflate(inflater, container, false)

        binding.apply {

            util.showSoftInputFragment(feedSearch, activity)
            /*feedSearch.apply{
                isFocusableInTouchMode = true
                requestFocus()
            }*/

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
        /*val fragmentManager = activity?.supportFragmentManager
        fragmentManager?.beginTransaction()
            ?.replace(R.id.fl_container, FeedFragment())
            ?.addToBackStack(null)
            ?.commit()*/
        // activity?.supportFragmentManager?.popBackStack()

        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }


}