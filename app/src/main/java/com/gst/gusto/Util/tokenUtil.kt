package com.gst.gusto.util

import android.content.Context
    fun Context.getSharedPref(): Pair<String, String> {
        val sharedPref = this.getSharedPreferences("token_pref", Context.MODE_PRIVATE)
        // 액세스 토큰과 리프레시 토큰을 가져오는 함수
        val accessToken = sharedPref.getString("accessToken", "")?: ""
        val refreshToken = sharedPref.getString("refreshToken", "")?: ""
        return Pair(accessToken, refreshToken)
    }
