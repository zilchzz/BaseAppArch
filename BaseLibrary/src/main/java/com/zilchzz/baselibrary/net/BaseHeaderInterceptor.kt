package com.zilchzz.baselibrary.net

import okhttp3.Interceptor
import okhttp3.Response


/**
 * Created by cy on 2018/7/17.
 */
class BaseHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response? {
        val builder = chain?.request()?.newBuilder()
        return chain?.proceed(builder?.build())
    }
}