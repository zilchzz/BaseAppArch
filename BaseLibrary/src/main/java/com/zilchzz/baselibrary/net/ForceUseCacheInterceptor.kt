package com.zilchzz.baselibrary.net

import com.zilchzz.baselibrary.BaseApplication
import com.zilchzz.baselibrary.ext.isNetworkConnected
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

class ForceUseCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        //在没有网络并且是 GET 请求时，强制使用最近的缓存
        if (!BaseApplication.get().isNetworkConnected() && request.method == "GET") {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
        }
        return chain.proceed(request)
    }
}