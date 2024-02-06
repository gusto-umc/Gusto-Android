package com.gst.gusto.api

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.annotations.SerializedName
import com.gst.gusto.BuildConfig
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel: ViewModel() {
    val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_BASE)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val service = retrofit.create(LoginRetrofit::class.java)

    private lateinit var image : String
    private lateinit var tmpToken : String
    private lateinit var nickName : String
    private lateinit var age : String
    private lateinit var gender : String
    private lateinit var accessToken : String
    private lateinit var refreshToken : String
    fun setImage(image : String) : Boolean {
        this.image = image
        return true
    }
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
    fun setAccessToken(accessToken : String) : Boolean  {
        this.accessToken = accessToken
        return true
    }
    fun setRefreshToken(refreshToken : String) : Boolean  {
        this.refreshToken = refreshToken
        return true
    }
    fun getAccessToken() : String  {
        return accessToken
    }
    fun getRefreshToken() : String  {
        return refreshToken
    }
    fun signUp(callback: (Int) -> Unit){
        val info = """
            {
                "nickname": "$nickName",
                "age": "$age",
                "gender": "$gender",
                "profileImg" : "$image"
            }
        """.trimIndent().toRequestBody("application/json".toMediaTypeOrNull())
        service.signUp(tmpToken, null,info)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        accessToken = response.headers().get("X-Auth-Token")?:""
                        refreshToken = response.headers().get("Refresh-Token")?:""
                        //Log.d("get tokens","$accessToken, $refreshToken")
                        callback(1)
                    } else {
                        Log.e("LoginViewModel", "Unsuccessful response: ${response}")
                        callback(2)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("LoginViewModel", "Failed to make the request", t)
                    callback(2)
                }
            })
    }
    fun checkNickname(nickname : String, callback: (Int) -> Unit){
        Log.d("nickname",nickname)
        service.checkNickname(nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("code",response.code().toString())
                if (response.isSuccessful) {
                    //Log.d("code",response.code().toString())
                    callback(1)
                } else if(response.code() == 409) {
                    callback(2)
                } else {
                    Log.e("LoginViewModel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("LoginViewModel", "Failed to make the request", t)
                callback(3)
            }
        })
    }
    fun confirmNickname(nickname : String, callback: (Int) -> Unit){
        service.checkNickname(nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("code",response.code().toString())
                if (response.isSuccessful) {
                    callback(1)
                } else if(response.code() == 409) {
                    callback(2)
                } else {
                    Log.e("LoginViewModel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("LoginViewModel", "Failed to make the request", t)
                callback(3)
            }
        })
    }

}