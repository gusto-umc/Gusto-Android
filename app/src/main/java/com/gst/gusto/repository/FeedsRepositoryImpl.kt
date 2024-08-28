package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.datasource.FeedsDataSource
import com.gst.gusto.model.InstaReview

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

}