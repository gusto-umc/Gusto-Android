package com.gst.gusto.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("token_pref", Context.MODE_PRIVATE)

    fun getSharedPrefs(): Pair<String, String> {
        val accessToken = prefs.getString("accessToken", "")?: ""
        val refreshToken = prefs.getString("refreshToken", "")?: ""
        return Pair(accessToken, refreshToken)
    }

    fun setSharedPrefs(accessToken: String, refreshToken: String) {
        val editor = prefs.edit()
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)

        editor.apply()
    }
}