package com.zilchzz.baselibrary.net

import okhttp3.Interceptor
import okhttp3.Response

class NetworkCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var maxAge = request.cacheControl.maxAgeSeconds
        if (maxAge == -1)
            maxAge = 20
        val response = chain.proceed(request)
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
    }
}