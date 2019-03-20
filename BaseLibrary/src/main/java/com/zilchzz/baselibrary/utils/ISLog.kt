package com.zilchzz.baselibrary.utils

import android.util.Log
import com.zilchzz.baselibrary.BuildConfig

/**
 * Log日志简单实现
 *
 * @author zhongzhanzhong 2018-04-05 21:32
 */
class ISLog {
    companion object {
        private const val TAG = "FCAT_LOG_TAG"

        fun e(tag: String, message: String?) {
            if (BuildConfig.DEBUG) {
                Log.e(tag, message)
            }
        }

        fun d(tag: String, message: String?) {
            if (BuildConfig.DEBUG) {
                Log.d(tag, message)
            }
        }

        fun d(message: String?) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, message)
            }
        }

        fun e(message: String?) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, message)
            }
        }
    }
}