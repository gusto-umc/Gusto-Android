package com.gst.gusto.datasource

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.dto.ResponseFeedReview
import com.gst.gusto.api.service.FeedsApi

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
}