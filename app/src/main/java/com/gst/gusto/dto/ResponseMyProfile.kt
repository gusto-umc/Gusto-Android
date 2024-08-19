package com.gst.gusto.dto

import com.gst.gusto.model.MyProfileData

data class ResponseMyProfile(
    val age: String,
    val gender: String,
    val nickname: String,
    val profileImg: String
) {
    fun toDomainModel(): MyProfileData {
        return MyProfileData(
            age = age,
            gender = gender,
            nickname = nickname,
            profileImg = profileImg
        )
    }
}