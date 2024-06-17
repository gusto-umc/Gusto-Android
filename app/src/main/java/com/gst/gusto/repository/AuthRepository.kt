package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.dto.ResponseRefreshToken
import okhttp3.ResponseBody

interface AuthRepository {
    suspend fun getRefreshToken(xAuthToken: String, refreshToken: String): ApiResponse<ResponseRefreshToken>
}