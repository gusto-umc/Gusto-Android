package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.retrofit.RetrofitInstance
import com.gst.gusto.api.service.UsersApi
import com.gst.gusto.datasource.UsersDataSource
import com.gst.gusto.model.MyPublishData

interface UsersRepository {
    suspend fun getMyPublish(token: String): ApiResponse<MyPublishData>

    companion object {
        fun create(): UsersRepositoryImpl{
            return UsersRepositoryImpl(
                UsersDataSource(
                    RetrofitInstance.createService(UsersApi::class.java)
                )
            )
        }
    }
}