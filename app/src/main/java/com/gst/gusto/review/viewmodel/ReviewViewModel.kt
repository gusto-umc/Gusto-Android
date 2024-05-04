package com.gst.gusto.review.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gst.gusto.api.ApiResponse
import com.gst.gusto.model.InstaReview
import com.gst.gusto.repository.AuthRepositoryImpl
import com.gst.gusto.repository.ReviewsRepositoryImpl
import com.gst.gusto.util.GustoApplication
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewsRepository: ReviewsRepositoryImpl,
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {

    private val xAuthToken = GustoApplication.prefs.getSharedPrefs().first
    private val refreshToken = GustoApplication.prefs.getSharedPrefs().second

    private val _instaReviews = MutableLiveData<List<InstaReview>>()
    val instaReviews : LiveData<List<InstaReview>> = _instaReviews

    private val _instaCursorId = MutableLiveData<Long>()
    val instaCursorId : LiveData<Long> = _instaCursorId

    private val _instaHasNext = MutableLiveData<Boolean>()
    val instaHasNext : LiveData<Boolean> = _instaHasNext

    private val _tokenToastData: MutableLiveData<Unit> = MutableLiveData()
    val tokenToastData: LiveData<Unit> = _tokenToastData

    init {
        viewModelScope.launch {
            getInstaReview(xAuthToken, null, 18)
        }

    }

    suspend fun getInstaReview(token: String, reviewId: Long?, size: Int) {
        when (val response = reviewsRepository.getInstaReview(token, reviewId, size)) {
            is ApiResponse.Success -> {
                _instaReviews.value = response.data.reviews
                _instaCursorId.value = response.data.cursorId
                _instaHasNext.value = response.data.hasNext
            }

            is ApiResponse.Error -> {
                if (response.errorCode == 403) {
                    _tokenToastData.value = Unit
                    setRefreshToken()
                }
            }
        }
    }

    suspend fun setRefreshToken() = viewModelScope.launch {
        while (true) {
            when (val response = authRepository.getRefreshToken(xAuthToken, refreshToken)) {
                is ApiResponse.Success -> {
                    GustoApplication.prefs.setSharedPrefs(
                        response.data.xAuthToken,
                        response.data.refreshToken
                    )
                    break
                }
                is ApiResponse.Error -> {
                    if (response.errorCode != 403) {
                        break
                    }
                }
            }
        }
    }

}