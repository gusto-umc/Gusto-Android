package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.model.InstaReviews
import com.gst.gusto.model.TimeLineReviews

interface ReviewsRepository {
    suspend fun getInstaReview(token: String, reviewId: Long?, size: Int): ApiResponse<InstaReviews>

    suspend fun getOtherInstaReview(token: String, nickname: String, reviewId: Long?, size: Int): ApiResponse<InstaReviews>

    suspend fun getTimeLineReview(token: String, reviewId: Long?, size: Int): ApiResponse<TimeLineReviews>

}