package com.gst.gusto.datasource

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.service.UsersApi
import com.gst.gusto.dto.RequestMyPublish
import com.gst.gusto.dto.ResponseMyPublish
import okhttp3.ResponseBody

class UsersDataSource(
    private val usersService: UsersApi
) {
    suspend fun getMyPublish(
        token: String
    ): ApiResponse<ResponseMyPublish> {
        val response = usersService.getMyPublish(token)
        return if(response.isSuccessful){
            response.body()?.let{
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(), "Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }

    suspend fun setMyPublish(
        token: String,
        myPublish: RequestMyPublish
    ): ApiResponse<ResponseBody> {
        val response = usersService.setMyPublish(token, myPublish)
        return if(response.isSuccessful){
            response.body()?.let{
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(), "Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }
}