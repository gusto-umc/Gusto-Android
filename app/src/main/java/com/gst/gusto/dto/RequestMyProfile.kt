package com.gst.gusto.dto

import com.google.gson.GsonBuilder
import com.gst.gusto.model.MyProfileData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

data class RequestMyProfile(
    val nickname: String?,
    val age: String?,
    val gender: String?,
    val useDefaultImg: Boolean
)

fun MyProfileData.toRequestBody(): RequestBody {
    val gson = GsonBuilder().serializeNulls().create()

    val requestMyProfile = RequestMyProfile(
        nickname = this.nickname,
        age = this.age,
        gender = this.gender,
        useDefaultImg = (this.profileImg == null)
    )

    val settingJson = gson.toJson(requestMyProfile)
    return settingJson.toRequestBody("application/json".toMediaTypeOrNull())
}
