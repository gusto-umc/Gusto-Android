package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.datasource.FeedsDataSource
import com.gst.gusto.model.InstaReview
import com.gst.gusto.model.InstaReviews

class FeedsRepositoryImpl(
    private val feedsDataSource: FeedsDataSource
): FeedsRepository {
    override suspend fun getFeed(token: String): ApiResponse<List<InstaReview>> {
        return when (val response = feedsDataSource.getFeed(token)){
            is ApiResponse.Success -> {
                val domainModels = response.data.map { it.toDomainModel() }
                ApiResponse.Success(domainModels)
            }
            is ApiResponse.Error -> ApiResponse.Error(response.errorCode, response.errorMessage)
        }
    }

    override suspend fun getSearchFeed(
        token: String,
        keyWord: String,
        hasTag: List<Long>?,
        reviewId: Long?,
        size: Int
    ): ApiResponse<InstaReviews> {
        return when (val response = feedsDataSource.getFeedSearch(token, keyWord, hasTag, reviewId, size)){
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomainModel())
            is ApiResponse.Error -> ApiResponse.Error(response.errorCode, response.errorMessage)
        }
    }

}