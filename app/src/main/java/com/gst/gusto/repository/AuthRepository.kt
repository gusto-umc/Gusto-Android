package com.gst.gusto.repository

import com.google.android.gms.auth.api.Auth
import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.retrofit.RetrofitInstance
import com.gst.gusto.api.service.AuthApi
import com.gst.gusto.datasource.AuthDataSource
import com.gst.gusto.dto.ResponseRefreshToken
import okhttp3.ResponseBody

interface AuthRepository {
    suspend fun getRefreshToken(
        xAuthToken: String,
        refreshToken: String
    ): ApiResponse<ResponseRefreshToken>

    companion object {
        fun create(): AuthRepositoryImpl {
            return AuthRepositoryImpl(
                AuthDataSource(
                    RetrofitInstance.createService(AuthApi::class.java)
                )
            )
        }
    }
}