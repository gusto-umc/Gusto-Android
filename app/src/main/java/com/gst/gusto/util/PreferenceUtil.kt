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

    fun setSharedPrefsBoolean(key : String, value : Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)

        editor.apply()
    }
    fun setSharedPrefsString(key : String, value : String) {
        val editor = prefs.edit()
        editor.putString(key, value)

        editor.apply()
    }
    fun getSharedPrefsBoolean(key : String) : Boolean{
        val returnValue = prefs.getBoolean(key, false)?: false
        return returnValue
    }
    fun getSharedPrefsString(key : String) : String{
        val returnValue = prefs.getString(key, "")?:""
        return returnValue
    }
}