package com.gst.gusto.dto

import com.google.gson.annotations.SerializedName
import com.gst.gusto.model.TimeLineReview
import com.gst.gusto.model.TimeLineReviews
import com.gst.gusto.review.adapter.ListReviewType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// list (timeline) review 조회- reviews
data class ResponseTimeLineReview(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("visitedAt") val visitedAt: Date,
    @SerializedName("visitedCount") val visitedCount: Int,
    @SerializedName("images") val images: List<String>,
) {
    fun toDomainModel(): TimeLineReview{
        val DF = SimpleDateFormat("MM/dd", Locale.KOREAN)
        return TimeLineReview(
            date = DF.format(visitedAt).toString(),
            name = storeName,
            visit = visitedCount.toString(),
            images = images,
            reviewId  = reviewId,
            viewType = ListReviewType.LISTREVIEW
        )
    }
}

// list (timeline) review 조회
data class ResponseTimeLineReviews(
    @SerializedName("reviews") val reviews: List<ResponseTimeLineReview>,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("cursorId") val cursorId: Long
) {
    fun toDomainModel(): TimeLineReviews {
        return TimeLineReviews(
            reviews = reviews.map {it.toDomainModel()},
            hasNext = hasNext,
            cursorId = cursorId
        )
    }
}
