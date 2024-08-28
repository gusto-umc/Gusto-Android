package com.gst.gusto.feed.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gst.gusto.api.ApiResponse
import com.gst.gusto.model.InstaReview
import com.gst.gusto.repository.AuthRepositoryImpl
import com.gst.gusto.repository.FeedsRepositoryImpl
import com.gst.gusto.util.GustoApplication
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedsRepository: FeedsRepositoryImpl,
    private val authRepository: AuthRepositoryImpl
): ViewModel() {
    private val xAuthToken = GustoApplication.prefs.getSharedPrefs().first
    private val refreshToken = GustoApplication.prefs.getSharedPrefs().second

    private val _tokenToastData: MutableLiveData<Unit> = MutableLiveData()
    val tokenToastData: LiveData<Unit> = _tokenToastData

    private val _errorToastData: MutableLiveData<Unit> = MutableLiveData()
    val errorToastData: LiveData<Unit> = _errorToastData

    private val _feedReview: MutableLiveData<List<InstaReview>> = MutableLiveData()
    val feedReview: LiveData<List<InstaReview>> = _feedReview

    private val _scrollData: MutableLiveData<Unit> = MutableLiveData()
    val scrollData: LiveData<Unit> = _scrollData

    private var isFetching = false

    init {
        viewModelScope.launch {
            _feedReview.value = getFeed().toMutableList()
        }
    }

    suspend fun getFeed(): List<InstaReview> {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when(val response = feedsRepository.getFeed(token)){
            is ApiResponse.Success -> {
               return response.data
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

    fun onScrolled() {
        if (isFetching) return
        val previousItems: List<InstaReview> = _feedReview.value ?: emptyList()

        viewModelScope.launch {
            isFetching = true
            val newItems = getFeed()
            _feedReview.value = previousItems + newItems
            isFetching = false

            _scrollData.value = Unit
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