package com.gst.gusto.api

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import com.gst.gusto.BuildConfig
import com.gst.gusto.util.util
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URI
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LoginViewModel: ViewModel() {
    val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_BASE)
        .addConverterFactory(GsonConverterFactory.create()).build()

    val service = retrofit.create(LoginApi::class.java)

    private lateinit var image : String
    private var accessToken = ""
    private var refreshToken = ""

    lateinit var provider : String
    var providerId  = ""
    var socialAccessToken = ""

    var profileImg : File?=null
    var profileUrl : String?=""
    lateinit var nickName : String
    lateinit var age : String
    lateinit var gender : String
    fun setImage(image : String) : Boolean {
        this.image = image
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
    fun getAccessToken() : String  {
        return accessToken
    }
    fun getRefreshToken() : String  {
        return refreshToken
    }
    fun signUp(callback: (Int) -> Unit){
        var encryptAccessToken = encryptAES256(socialAccessToken)
        var encryptProviderId = encryptAES256(providerId)
        val info = Singup(provider,encryptProviderId,encryptAccessToken,nickName,age,gender)
        Log.d("SOCIAL LOGIN INFO2", "${provider}, ${providerId}, ${socialAccessToken}")
        var profileMutipart : MultipartBody.Part?=null
        if(profileImg!=null) {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), profileImg!!)
            profileMutipart = MultipartBody.Part.createFormData("profileImg", profileImg!!.name, requestFile)
        }

        service.signUp(profileMutipart,info)
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
    fun login(callback: (Int) -> Unit){
        var encryptAccessToken = encryptAES256(socialAccessToken)
        var encryptProviderId = encryptAES256(providerId)
        //Log.d("SOCIAL LOGIN INFO123", "${provider}, ${providerId}, ${tmp} , ${socialAccessToken}")

        service.login(Login(provider,encryptProviderId,encryptAccessToken))
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        accessToken = response.headers().get("X-Auth-Token")?:""
                        refreshToken = response.headers().get("Refresh-Token")?:""
                        //Log.d("get tokens","$accessToken, $refreshToken")
                        callback(1)
                    }else if(response.code() == 404) {
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
    fun randomNickname(callback: (Int, String) -> Unit){
        service.randomNickname()
            .enqueue(object : Callback<Nickname> {
                override fun onResponse(call: Call<Nickname>, response: Response<Nickname>) {
                    if (response.isSuccessful) {
                        if(response.body()!=null) {
                            callback(1, response.body()!!.nickname)
                        } else {
                            callback(2,"")
                        }
                    } else {
                        Log.e("LoginViewModel", "Unsuccessful response: ${response}")
                        callback(3,"")
                    }
                }

                override fun onFailure(call: Call<Nickname>, t: Throwable) {
                    Log.e("LoginViewModel", "Failed to make the request", t)
                    callback(3,"")
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
        service.confirmNickname(nickname).enqueue(object : Callback<ResponseBody> {
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

    fun test(str : String) {
        service.test(str).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("code",response.code().toString())
                if (response.isSuccessful) {
                    Log.e("AES3", "Successful response: ${response}")
                } else {
                    Log.e("AES3", "Unsuccessful response: ${response}")

                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("AES3", "Failed to make the request", t)

            }
        })
    }

    var secretStr = "Z8BQ41_PRIVATE_KEY_GUSTO_5F573EA"
    var ivBytes = "dccmCIrBNboegEJ1".toByteArray(Charsets.UTF_8)
    fun encryptAES256(input: String): String {
        // Convert the secret key string to a byte array
        val keyBytes = secretStr.toByteArray(Charsets.UTF_8)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")

        // Convert the fixed IV string to a byte array
        val ivParameterSpec = IvParameterSpec(ivBytes)

        // Initialize the cipher for encryption
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

        // Encrypt the input string
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))

        // Encode the result to Base64 to make it easier to handle
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    fun decryptAES256(input: String): String {
        // Convert the secret key string to a byte array
        val keyBytes = secretStr.toByteArray(Charsets.UTF_8)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")

        // Convert the fixed IV string to a byte array
        val ivParameterSpec = IvParameterSpec(ivBytes)

        // Decode the Base64 encoded input
        val encryptedBytes = Base64.decode(input, Base64.DEFAULT)

        // Initialize the cipher for decryption
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

        // Decrypt the bytes and convert to a string
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

}