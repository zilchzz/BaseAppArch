package com.zilchzz.baselibrary.net

import android.text.TextUtils
import com.zilchzz.baselibrary.utils.LoginStatus
import com.zilchzz.baselibrary.ext.getCommonRequestMap
import com.zilchzz.baselibrary.ext.getSign
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class EncryptInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if ("POST" == request.method) { //是POST请求
            val requestBody = request.body
            //step 1,获取所有参数对
            val commonParamsMap = getCommonRequestMap()
            val paramsMap = TreeMap<String, String>()
            //是表单提交的话，将表单里面的所有参数put进去，如果不是则可能该POST请求没有参数
            if (requestBody is FormBody) {
                (0 until requestBody.size).forEach {
                    commonParamsMap[requestBody.encodedName(it)] = requestBody.encodedValue(it)
                }
            }
            commonParamsMap.putAll(paramsMap)

            //step 2,整合参数，拿到sign，构建新的请求参数
            val finalParamsBody = FormBody.Builder()
            for ((key, value) in commonParamsMap) {
                finalParamsBody.add(key, value)
            }
            val sign = getSign(commonParamsMap)
            finalParamsBody.addEncoded("sign", sign)

            request = request.newBuilder().post(finalParamsBody.build()).build()
        }
        //step 3,如果sid或者api key 跟 secret 不为空的话，传token跟cookie
        val builder = request.newBuilder()
        if (!TextUtils.isEmpty(LoginStatus.sid)) {
            builder.addHeader("COOKIE", "sid=${LoginStatus.sid}")
        }
        val encryptApiKey = EncryptUtils.getEncryptAPIKEY()
        if (!TextUtils.isEmpty(encryptApiKey)) {
            builder.addHeader("TOKEN", EncryptUtils.getEncryptAPIKEY())
        }
        return chain.proceed(builder.build())
    }
}