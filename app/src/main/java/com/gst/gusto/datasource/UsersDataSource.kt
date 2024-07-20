package com.gst.gusto.datasource

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.service.UsersApi
import com.gst.gusto.dto.ResponseGetMyPublish

class UsersDataSource(
    private val usersService: UsersApi
) {
    suspend fun getMyPublish(
        token: String
    ): ApiResponse<ResponseGetMyPublish> {
        val response = usersService.getMyPublish(token)
        return if(response.isSuccessful){
            response.body()?.let{
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(), "Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }
}