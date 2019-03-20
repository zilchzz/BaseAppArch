package com.zilchzz.baselibrary.net

import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

/**
 * Created by cy on 2018/8/8.
 */
object SSLContextHelper {
    private var sslContext: SSLContext? = null

    @Synchronized
    fun getSSLContext(): SSLContext {
        if (sslContext == null) {
            // 忽略HTTPS效验
            val trustAllCerts: Array<TrustManager> = arrayOf(TrustAllManager())
            try {
                sslContext = SSLContext.getInstance("SSL")
                sslContext!!.init(null, trustAllCerts, java.security.SecureRandom())
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
        }
        return sslContext!!
    }
}