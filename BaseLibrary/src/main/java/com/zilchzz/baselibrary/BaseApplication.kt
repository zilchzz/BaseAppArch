package com.zilchzz.baselibrary

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport

class BaseApplication : Application() {
    companion object {
        private lateinit var context: Application

        fun get() = context

    }

    override fun onCreate() {
        super.onCreate()
        context = this
        initBugly()
    }

    private fun initBugly() {
        val strategy = CrashReport.UserStrategy(context)
        strategy.appChannel = "official"
        if (BuildConfig.DEBUG) {
            CrashReport.initCrashReport(this, "3c77765300", true)
        } else {
            CrashReport.initCrashReport(this, "c9f816ef98", false)
        }
    }
}