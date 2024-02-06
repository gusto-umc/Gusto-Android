package com.gst.api

import android.util.Log
import androidx.lifecycle.ViewModel
import com.gst.gusto.BuildConfig
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel: ViewModel() {
    val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_BASE)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val service = retrofit.create(LoginRetrofit::class.java)

    lateinit var tmpToken : String
    lateinit var nickName : String
    lateinit var age : String
    lateinit var gender : String
    fun setTempToken(tmpToken : String) : Boolean {
        this.tmpToken = tmpToken
        return true
    }
    fun setNickName(nickName : String) : Boolean  {
        this.nickName = nickName
        return true
    }
    fun setAge(age : String) : Boolean  {
        this.age = age
        return true
    }
    fun setGender(gender : String) : Boolean  {
        this.gender = gender
        return true
    }
    fun signUp(callback: (Int) -> Unit){
        val profileImgPart: MultipartBody.Part? = null
        val infoJson = """
            {
                "nickname": "$nickName",
                "age": "$age",
                "gender": "$gender"
            }
        """.trimIndent()
        service.signUp(tmpToken,infoJson)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Log.d("responseBody", responseBody.toString())
                            callback(1)
                        } else {
                            Log.e("LoginViewModel", "Response body is null")
                            callback(2)
                        }
                    } else {
                        Log.e("LoginViewModel", "Unsuccessful response: ${response.code()}")
                        callback(2)
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("LoginViewModel", "Failed to make the request", t)
                    callback(2)
                }
            })

    }
}