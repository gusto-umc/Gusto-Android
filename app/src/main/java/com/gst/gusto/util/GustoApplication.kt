package com.gst.gusto.util

import android.app.Application
import com.gst.gusto.util.PreferenceUtil

class GustoApplication: Application() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceUtil(applicationContext)
    }
}