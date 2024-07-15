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
import com.gst.gusto.review.fragment.CalendarReviewFragment
import com.gst.gusto.review.fragment.InstaReviewFragment
import com.gst.gusto.review.fragment.ListReviewFragment
import com.gst.gusto.review.viewmodel.InstaReviewViewModel
import com.gst.gusto.review.viewmodel.InstaReviewViewModelFactory

class ReviewFragment : Fragment() {

    lateinit var binding: FragmentReviewBinding

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
        setFragment()
    }

    private fun setFragment() {
        val fragmentMananger = childFragmentManager.beginTransaction()
        fragmentMananger.replace(R.id.review_fragment, InstaReviewFragment())
        // fragmentMananger.replace(R.id.review_fragment, CalendarReviewFragment())
        // fragmentMananger.replace(R.id.review_fragment, ListReviewFragment())
        fragmentMananger.commit()

    }

}
