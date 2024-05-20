package com.gst.gusto.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gst.gusto.api.retrofit.RetrofitInstance
import com.gst.gusto.api.service.AuthApi
import com.gst.gusto.api.service.ReviewsApi
import com.gst.gusto.datasource.AuthDataSource
import com.gst.gusto.datasource.ReviewsDataSource
import com.gst.gusto.repository.AuthRepositoryImpl
import com.gst.gusto.repository.ReviewsRepositoryImpl
import java.lang.IllegalArgumentException

class MyReviewViewModelFactory(): ViewModelProvider.Factory {
    private val reviewsRepository = ReviewsRepositoryImpl(
        ReviewsDataSource(RetrofitInstance.createService(ReviewsApi::class.java)),
    )
    private val authRepository = AuthRepositoryImpl(
        AuthDataSource(RetrofitInstance.createService(AuthApi::class.java))
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyReviewViewModel::class.java)){
            return MyReviewViewModel(reviewsRepository, authRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}