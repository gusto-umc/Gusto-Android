package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.retrofit.RetrofitInstance
import com.gst.gusto.api.service.UsersApi
import com.gst.gusto.datasource.UsersDataSource
import com.gst.gusto.model.MyPublishData
import okhttp3.ResponseBody

interface UsersRepository {
    suspend fun getMyPublish(token: String): ApiResponse<MyPublishData>
    suspend fun setMyPublish(token: String, myPublish: MyPublishData): ApiResponse<ResponseBody>

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