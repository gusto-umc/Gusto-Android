package com.gst.gusto.api.service

import com.gst.gusto.api.ResponseFeedSearchReviews
import com.gst.gusto.dto.ResponseFeedReview
import com.gst.gusto.dto.ResponseFeedSearch
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FeedsApi {
    @GET("feeds") // 먹스또 랜덤 피드
    suspend fun getFeed(
        @Header("X-AUTH-TOKEN") token: String
    ): Response<List<ResponseFeedReview>>

    @GET("feeds/search") // 맛집 & 해시태그 검색 엔진
    suspend fun getFeedSearch(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("keyword") keyword: String,
        @Query("hashTags") hashTags: List<Long>?,
        @Query("reviewId") reviewId: Long?,
        @Query("size") size: Int
    ): Response<ResponseFeedSearch>
}