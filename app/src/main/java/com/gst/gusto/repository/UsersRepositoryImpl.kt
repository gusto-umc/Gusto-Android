package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.datasource.UsersDataSource
import com.gst.gusto.model.MyPublishData

class UsersRepositoryImpl(
    private val usersDataSource: UsersDataSource
): UsersRepository {
    override suspend fun getMyPublish(token: String): ApiResponse<MyPublishData> {
        return when(val response = usersDataSource.getMyPublish(token)){
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomainModel())
            is ApiResponse.Error -> ApiResponse.Error(response.errorCode, response.errorMessage)
        }
    }
}