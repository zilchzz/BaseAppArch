package com.zilchzz.baselibrary.net

import android.util.Log
import com.zilchzz.baselibrary.BaseApplication
import com.zilchzz.baselibrary.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * 生成Retrofit实例
 * base_url https://api.fcat.com/
 */

object RetrofitHelper {
    private var DEFAULT_TIME_OUT = 20L

    var mRetrofit: Retrofit? = null

    fun getRetrofit(): Retrofit = if (mRetrofit == null) initRetrofit() else mRetrofit!!

    private fun initRetrofit(): Retrofit {
        synchronized(RetrofitHelper::class.java) {
            if (mRetrofit == null) {
                val httpClientBuilder = OkHttpClient.Builder()
                        .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                        .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                        .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                        .sslSocketFactory(SSLContextHelper.getSSLContext().socketFactory, TrustAllManager())
                        .hostnameVerifier { hostname, _ -> hostname == ServerConfig.HOST_NAME }

                //配置缓存
                httpClientBuilder.cache(Cache(File(BaseApplication.get().cacheDir, "okhttp"), 10 * 1024 * 1024))
                httpClientBuilder.addNetworkInterceptor(NetworkCacheInterceptor())
                httpClientBuilder.addInterceptor(ForceUseCacheInterceptor())

                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                        Log.d("okhttp", message)
                    })
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    httpClientBuilder.addInterceptor(loggingInterceptor)
                }
                mRetrofit = Retrofit.Builder()
                        .baseUrl(ServerConfig.BASE_URL)
                        .client(httpClientBuilder.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
            }
        }
        return mRetrofit!!
    }
}