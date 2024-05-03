package com.gst.gusto.review.viewmodel

import androidx.lifecycle.ViewModel
import com.gst.gusto.repository.ReviewsRepositoryImpl

class ReviewViewModel(
    private val reviewsRepository: ReviewsRepositoryImpl
) : ViewModel() {

}