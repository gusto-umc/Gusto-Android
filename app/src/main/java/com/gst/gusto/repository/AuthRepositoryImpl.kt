package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.datasource.AuthDataSource
import com.gst.gusto.dto.ResponseRefreshToken
import okhttp3.ResponseBody

class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override suspend fun getRefreshToken(
        xAuthToken: String,
        refreshToken: String
    ): ApiResponse<ResponseRefreshToken> {
        return when (val response = authDataSource.getRefreshToken(xAuthToken, refreshToken)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data)
            is ApiResponse.Error -> ApiResponse.Error(response.errorCode, response.errorMessage)
        }
    }

}