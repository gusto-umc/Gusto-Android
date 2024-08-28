package com.gst.gusto.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gst.gusto.api.retrofit.RetrofitInstance
import com.gst.gusto.api.service.AuthApi
import com.gst.gusto.api.service.FeedsApi
import com.gst.gusto.datasource.AuthDataSource
import com.gst.gusto.datasource.FeedsDataSource
import com.gst.gusto.repository.AuthRepositoryImpl
import com.gst.gusto.repository.FeedsRepositoryImpl
import java.lang.IllegalArgumentException

class FeedViewModelFactory(): ViewModelProvider.Factory {
    private val feedsRepository = FeedsRepositoryImpl(
        FeedsDataSource(RetrofitInstance.createService(FeedsApi::class.java)),
    )
    private val authRepository = AuthRepositoryImpl(
        AuthDataSource(RetrofitInstance.createService(AuthApi::class.java))
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)){
            return FeedViewModel(feedsRepository, authRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}