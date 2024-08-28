package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.model.InstaReview

interface FeedsRepository {
    suspend fun getFeed(token: String): ApiResponse<List<InstaReview>>
}