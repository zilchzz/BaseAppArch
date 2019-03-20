package com.zilchzz.baselibrary.net

import com.google.gson.Gson
import com.zilchzz.baselibrary.BuildConfig
import com.zilchzz.baselibrary.utils.ISLog
import com.zilchzz.baselibrary.net.bean.UpdateBean
import com.zilchzz.baselibrary.ext.getCommonRequestMap
import com.zilchzz.baselibrary.ext.getSign
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager


object UpdateHelper {
    private const val ADDRESS_CHECK_UPDATE =
            "${ServerConfig.BASE_URL}/api/common/appversion"

    private fun initContext(): SSLContext? {
        // 忽略HTTPS效验
        var sslContext: SSLContext? = null
        val trustAllCerts: Array<TrustManager> = arrayOf(TrustAllManager())
        try {
            sslContext = SSLContext.getInstance("SSL")
            sslContext!!.init(null, trustAllCerts, java.security.SecureRandom())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        return sslContext
    }

    fun doNetWorkToCheck(onReceivedUpdateMsgLis: OnReceivedUpdateMsgLis) {

        val okHttpClientBuilder = OkHttpClient.Builder().connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .sslSocketFactory(SSLContextHelper.getSSLContext().socketFactory!!, TrustAllManager())
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                ISLog.d("okhttp", message)
            })
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }

        //step 1,获取所有参数对
        val commonParamsMap = getCommonRequestMap()
        //step 2,整合参数，拿到sign，构建新的请求参数
        val finalParamsBody = FormBody.Builder()
        for ((key, value) in commonParamsMap) {
            finalParamsBody.add(key, value!!)
        }
        val sign = getSign(commonParamsMap)
        finalParamsBody.addEncoded("sign", sign!!)

        val request = Request.Builder()
                .url(ADDRESS_CHECK_UPDATE)
                .post(finalParamsBody.build())
                .build()

        val call = okHttpClientBuilder.build().newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                ISLog.e("检测升级失败")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val data = response?.body()?.string()
                if (data?.startsWith("{") == true) {
                    val jsonObject = JSONObject(data)
                    if (jsonObject.has("data")) {
                        val updateBean = Gson().fromJson(jsonObject.get("data").toString(), UpdateBean::class.java)
                        //有结果的话则回调到mainActivity
                        onReceivedUpdateMsgLis.onReceivedUpdateMsg(updateBean)
                    }
                }
            }
        })
    }

    fun doDownloadFile(url: String?, filePath: File, fileName: String, onDownProgressChangedLis: OnDownProgressChangedLis) {
        try {
            if (url == null) {
                return
            }
            //创建文件
            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            val destFile = File(filePath, fileName)
            val mOkHttpClient = OkHttpClient.Builder()
                    .sslSocketFactory(initContext()!!.socketFactory, TrustAllManager()).build()
            val request = Request.Builder().url(url).build()
            mOkHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onDownProgressChangedLis.onDownloadError()
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val contentLength = response.body()?.contentLength()
                        if (0L == contentLength) {
                            onDownProgressChangedLis.onDownloadError()
                            return
                        }
                        onDownProgressChangedLis.onStartDownload()
                        val bis = response.body()?.byteStream()
                        val bos = BufferedOutputStream(FileOutputStream(destFile))
                        val buffBytes = ByteArray(4096)
                        var length = bis?.read(buffBytes)
                        var currLength = 0L
                        while (length != -1) {
                            bos.write(buffBytes, 0, length!!)
                            length = bis?.read(buffBytes)
                            currLength += length!!
                            onDownProgressChangedLis.onProgressChanged(((currLength.toFloat() / contentLength!!) * 100).toInt())
                        }
                        bis?.close()
                        bos.close()
                        onDownProgressChangedLis.onFinishDownload()
                    } catch (e: IOException) {
                        onDownProgressChangedLis.onDownloadError()
                    }
                }
            })
        } catch (e: Exception) {
            onDownProgressChangedLis.onDownloadError()
        }
    }

    interface OnReceivedUpdateMsgLis {
        fun onReceivedUpdateMsg(updateBean: UpdateBean)
    }

    interface OnDownProgressChangedLis {
        fun onStartDownload()
        fun onProgressChanged(progress: Int)
        fun onFinishDownload()
        fun onDownloadError()
    }
}