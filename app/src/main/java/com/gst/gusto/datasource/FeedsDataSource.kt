package com.gst.gusto.datasource

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.dto.ResponseFeedReview
import com.gst.gusto.api.service.FeedsApi
import com.gst.gusto.dto.ResponseFeedSearch

class FeedsDataSource(
    private val feedsApi: FeedsApi
) {
    suspend fun getFeed(
        token: String
    ): ApiResponse<List<ResponseFeedReview>>{
        val response = feedsApi.getFeed(token)
        return if (response.isSuccessful){
            response.body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(),"Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }

    suspend fun getFeedSearch(
        token: String,
        keyWord: String,
        hasTag: List<Long>?,
        reviewId: Long?,
        size: Int
    ): ApiResponse<ResponseFeedSearch>{
        val response = feedsApi.getFeedSearch(token, keyWord, hasTag, reviewId, size)
        return if (response.isSuccessful){
            response.body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(),"Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }
}