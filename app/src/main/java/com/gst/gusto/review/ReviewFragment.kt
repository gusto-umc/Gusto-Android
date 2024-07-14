package com.gst.gusto.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentReviewBinding
import com.gst.gusto.review.fragment.InstaReviewFragment
import com.gst.gusto.review.viewmodel.InstaReviewViewModel
import com.gst.gusto.review.viewmodel.InstaReviewViewModelFactory

class ReviewFragment : Fragment() {

    lateinit var binding: FragmentReviewBinding

    val icons = listOf(R.drawable.gallery_review_img, R.drawable.calendar_review_img, R.drawable.list_review_img)

    private val gustoViewModel : GustoViewModel by activityViewModels()
    private val instaViewModel: InstaReviewViewModel by viewModels{ InstaReviewViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initViewPager()
        setFragment()
    }

    private fun setFragment() {
        val fragmentMananger = childFragmentManager.beginTransaction()
        fragmentMananger.replace(R.id.review_fragment, InstaReviewFragment())
        fragmentMananger.commit()

    }

    /*private fun initViewPager() {
        //ViewPager2 Adapter 셋팅
        var VPAdapter = ReviewAdapter(this)
        VPAdapter.addFragment(InstaReviewFragment())
        VPAdapter.addFragment(CalendarReviewFragment())
        VPAdapter.addFragment(ListReviewFragment())

        //Adapter 연결
        binding.reviewVP.apply {
            adapter = VPAdapter

        }

        // ViewPager TabLayout 연결
        TabLayoutMediator(binding.reviewTab, binding.reviewVP) { tab, position ->
            tab.setIcon(icons[position])
        }.attach()

        // 각 탭에 OnClickListener 설정
        for (i in 0 until binding.reviewTab.tabCount) {
            val tab = binding.reviewTab.getTabAt(i)
            tab?.view?.setOnClickListener {
                binding.reviewVP.currentItem = i
            }
        }

    }*/

}
