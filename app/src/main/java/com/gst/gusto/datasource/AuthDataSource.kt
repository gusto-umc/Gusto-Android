package com.gst.gusto.datasource

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.service.AuthApi
import com.gst.gusto.dto.ResponseRefreshToken
import okhttp3.ResponseBody

class AuthDataSource(
    private val authService: AuthApi
) {
    suspend fun getRefreshToken(
        xAuthToken: String,
        refreshToken: String
    ): ApiResponse<ResponseRefreshToken> {
        val response = authService.refreshToken(xAuthToken, refreshToken)
        return if (response.isSuccessful) {
            response.headers()?.let {
                ApiResponse.Success(
                    ResponseRefreshToken(
                        it.get("X-Auth-Token").toString(),
                        it.get("refresh-Token").toString()
                    )
                )
            } ?: ApiResponse.Error(response.code(), "Response body is null")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }
}