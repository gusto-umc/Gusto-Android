package com.gst.gusto.other.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gst.gusto.api.ApiResponse
import com.gst.gusto.model.InstaReview
import com.gst.gusto.repository.AuthRepositoryImpl
import com.gst.gusto.repository.ReviewsRepositoryImpl
import com.gst.gusto.util.GustoApplication
import kotlinx.coroutines.launch

class OtherReviewViewModel(
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

    private val _instaPagingData = MediatorLiveData<Pair<Long?, Boolean?>>()
    val instaPagingdata : LiveData<Pair<Long?, Boolean?>> = _instaPagingData

    private val _tokenToastData: MutableLiveData<Unit> = MutableLiveData()
    val tokenToastData: LiveData<Unit> = _tokenToastData

    private val _errorToastData: MutableLiveData<Unit> = MutableLiveData()
    val errorToastData: LiveData<Unit> = _errorToastData

    private var isFetching = false

    private val _scrollData: MutableLiveData<Unit> = MutableLiveData()
    val scrollData: LiveData<Unit> = _scrollData

    suspend fun getOtherInstaReview(nickname: String, size: Int) {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when (val response = reviewsRepository.getOtherInstaReview(token, nickname, null, size)) {
            is ApiResponse.Success -> {
                _instaReviews.value = response.data.reviews.toMutableList()

                _instaCursorId.value = response.data.cursorId
                _instaHasNext.value = response.data.hasNext
            }

            is ApiResponse.Error -> {
                if (response.errorCode == 403) {
                    _tokenToastData.value = Unit
                    setRefreshToken()
                } else {
                    _errorToastData.value = Unit
                }
            }
        }
    }

    fun onScrolled(nickname: String) {
        if (isFetching) return
        val previousItems: List<InstaReview> = _instaReviews.value ?: emptyList()

        val hasNext = _instaHasNext.value
        if (hasNext == true) {
            val cursorId = _instaCursorId.value
            viewModelScope.launch {
                isFetching = true
                val newItems = getOtherInstaReviewPaging(nickname, cursorId, 12)
                _instaReviews.value = previousItems + newItems
                Log.d("reviews", _instaReviews.value?.size.toString())
                isFetching = false
            }
        } else {
            _scrollData.value = Unit
        }
    }

    suspend fun getOtherInstaReviewPaging(nickname: String, reviewId: Long?, size: Int): List<InstaReview> {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when (val response = reviewsRepository.getOtherInstaReview(token, nickname, reviewId, size)) {
            is ApiResponse.Success -> {
                _instaCursorId.value = response.data.cursorId
                _instaHasNext.value = response.data.hasNext
                return response.data.reviews
            }

            is ApiResponse.Error -> {
                if (response.errorCode == 403) {
                    _tokenToastData.value = Unit
                    setRefreshToken()
                } else {
                    _errorToastData.value = Unit
                }
                return emptyList()
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