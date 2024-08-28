package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.model.InstaReview
import com.gst.gusto.model.InstaReviews

interface FeedsRepository {
    suspend fun getFeed(token: String): ApiResponse<List<InstaReview>>

    suspend fun getSearchFeed(token: String, keyWord: String, hasTag: List<Long>?, reviewId: Long?, size: Int): ApiResponse<InstaReviews>
}