package com.gst.gusto.review.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gst.gusto.api.ApiResponse
import com.gst.gusto.model.InstaReview
import com.gst.gusto.model.TimeLineReview
import com.gst.gusto.repository.AuthRepositoryImpl
import com.gst.gusto.repository.ReviewsRepositoryImpl
import com.gst.gusto.review.adapter.ListReviewType
import com.gst.gusto.util.GustoApplication
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ListReviewViewModel(
    private val reviewsRepository: ReviewsRepositoryImpl,
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {
    private val xAuthToken = GustoApplication.prefs.getSharedPrefs().first
    private val refreshToken = GustoApplication.prefs.getSharedPrefs().second

    private val _timeLineReviews = MutableLiveData<List<TimeLineReview>>()
    val timeLineReviews: LiveData<List<TimeLineReview>> = _timeLineReviews

    private val _timeLineCursorId = MutableLiveData<Long>()
    val timeLineCursorId : LiveData<Long> = _timeLineCursorId

    private val _timeLineHasNext = MutableLiveData<Boolean>()
    val timeLineHasNext : LiveData<Boolean> = _timeLineHasNext

    private val _tokenToastData: MutableLiveData<Unit> = MutableLiveData()
    val tokenToastData: LiveData<Unit> = _tokenToastData

    private val _errorToastData: MutableLiveData<Unit> = MutableLiveData()
    val errorToastData: LiveData<Unit> = _errorToastData

    private var isFetching = false

    private val _scrollData: MutableLiveData<Unit> = MutableLiveData()
    val scrollData: LiveData<Unit> = _scrollData

    init {
        viewModelScope.launch {
            getTimeLineReview(2)
        }
    }

    suspend fun getTimeLineReview(size: Int) {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when (val response = reviewsRepository.getTimeLineReview(token, null, size)) {
            is ApiResponse.Success -> {
                _timeLineReviews.value = response.data.reviews
                _timeLineCursorId.value = response.data.cursorId
                _timeLineHasNext.value = response.data.hasNext
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

    fun onScrolled() {
        if (isFetching) return
        val previousItems: List<TimeLineReview> = _timeLineReviews.value ?: emptyList()

        val hasNext = _timeLineHasNext.value
        if (hasNext == true) {
            val cursorId = _timeLineCursorId.value
            viewModelScope.launch {
                isFetching = true
                val newItems = getTimeLineReviewPaging(cursorId, 2)
                _timeLineReviews.value = previousItems + newItems
                isFetching = false
            }
        } else {
            _scrollData.value = Unit
        }
    }

    fun addLastData(){
        val NowTime = System.currentTimeMillis()
        val DF = SimpleDateFormat("MM/dd", Locale.KOREAN)
        val result = DF.format(NowTime).toString()

        val previousItems: List<TimeLineReview> = _timeLineReviews.value ?: emptyList()

        val lastItems : List<TimeLineReview> = listOf(TimeLineReview(result, "","", emptyList(), 0, ListReviewType.LISTBUTTON))

        _timeLineReviews.value = previousItems + lastItems
        Log.d("last data", timeLineReviews.value?.last().toString())

    }

    suspend fun getTimeLineReviewPaging(reviewId: Long?, size: Int): List<TimeLineReview> {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when (val response = reviewsRepository.getTimeLineReview(token, reviewId, size)) {
            is ApiResponse.Success -> {
                _timeLineCursorId.value = response.data.cursorId
                _timeLineHasNext.value = response.data.hasNext
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