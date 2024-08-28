package com.gst.gusto.feed.viewmodel

import android.util.Log
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
) : ViewModel() {
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

    private val _feedCursorId = MutableLiveData<Long>()
    val instaCursorId: LiveData<Long> = _feedCursorId

    private val _feedHasNext = MutableLiveData<Boolean>()
    val feedHasNext: LiveData<Boolean> = _feedHasNext

    private val _feedKeyWord = MutableLiveData<String>()
    val feedKeyWord: LiveData<String> = _feedKeyWord

    private val _feedHashTag = MutableLiveData<List<Long>?>()
    val feedHashTag: LiveData<List<Long>?> = _feedHashTag

    private var isFetching = false

    private var isSearching = false

    init {
        viewModelScope.launch {
            _feedReview.value = getFeed().toMutableList()
        }
    }

    suspend fun getFeed(): List<InstaReview> {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when (val response = feedsRepository.getFeed(token)) {
            is ApiResponse.Success -> {
                isSearching = false
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

    fun getFeedSearch(
        keyWord: String,
        hasTag: List<Long>?
    ) = viewModelScope.launch {
        val token = GustoApplication.prefs.getSharedPrefs().first

        _feedReview.value = emptyList()

        when (val response = feedsRepository.getSearchFeed(token, keyWord, hasTag, null, 6)) {
                is ApiResponse.Success -> {
                _feedReview.value = response.data.reviews
                _feedCursorId.value = response.data.cursorId
                _feedHasNext.value = response.data.hasNext
                _feedKeyWord.value = keyWord
                _feedHashTag.value = hasTag
                isSearching = true
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

    suspend fun getFeedSearchPaging(
        keyWord: String,
        hasTag: List<Long>?,
        reviewId: Long?
    ): List<InstaReview> {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when (val response = feedsRepository.getSearchFeed(token, keyWord, hasTag, reviewId, 6)) {
            is ApiResponse.Success -> {
                _feedCursorId.value = response.data.cursorId
                _feedHasNext.value = response.data.hasNext
                isSearching = true
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

    fun onFeedSearchScrolled(
    ) {
        if (isFetching) return
        val previousItems: List<InstaReview> = _feedReview.value ?: emptyList()

        val hasNext = _feedHasNext.value
        val keyword = _feedKeyWord.value.toString()
        val hasTag = _feedHashTag.value
        if (hasNext == true && isSearching) {
            val cursorId = _feedCursorId.value
            viewModelScope.launch {
                isFetching = true
                val newItems = getFeedSearchPaging(keyword, hasTag, cursorId)
                _feedReview.value = previousItems + newItems
                isFetching = false
            }
        } else {
            _scrollData.value = Unit
        }
    }

    fun onFeedScrolled() {
        if (isFetching) return
        val previousItems: List<InstaReview> = _feedReview.value ?: emptyList()

        if (!isSearching) {
            viewModelScope.launch {
                isFetching = true
                val newItems = getFeed()
                _feedReview.value = previousItems + newItems
                isFetching = false

                _scrollData.value = Unit
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