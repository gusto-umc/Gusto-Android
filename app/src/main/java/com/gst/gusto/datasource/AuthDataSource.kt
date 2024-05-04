package com.gst.gusto.datasource

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.service.AuthApi
import okhttp3.ResponseBody

class AuthDataSource(
    private val authService: AuthApi
) {
    suspend fun getRefreshToken(
        xAuthToken: String,
        refreshToken: String
    ): ApiResponse<ResponseBody> {
        val response = authService.refreshToken(xAuthToken, refreshToken)
        return if (response.isSuccessful) {
            response.body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(), "Response body is null")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }
}