package com.zilchzz.baselibrary.net

object EncryptUtils {

    var keySecret: String? = ""

    fun getEncryptAPIKEY(): String {
        if (keySecret?.isNotEmpty() == true) {
            return keySecret!!
        }
        return ""
    }
}