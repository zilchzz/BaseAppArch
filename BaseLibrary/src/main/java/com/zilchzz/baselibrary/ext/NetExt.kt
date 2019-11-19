package com.zilchzz.baselibrary.ext

import android.os.Build
import android.util.Log
import com.zilchzz.baselibrary.BaseApplication
import java.io.UnsupportedEncodingException
import java.util.*


/**
 * Created by cy on 2018/9/17.
 */
fun getSign(map: Map<String, String?>): String {
    val beforeMd5 = getFcatDatePrefix() + httpBuildQuery(map)
    Log.d("eeeee", beforeMd5)
    return beforeMd5.md5Encode()
}


private fun getFcatDatePrefix(): String {
    val calendar = Calendar.getInstance()
    calendar.time = Date(System.currentTimeMillis())
    val year = calendar.get(Calendar.YEAR)
    var month = (calendar.get(Calendar.MONTH) + 1).toString()
    var day = calendar.get(Calendar.DAY_OF_MONTH).toString()
    if (month.toInt() < 10) {
        month = "0$month"
    }
    if (day.toInt() < 10) {
        day = "0$day"
    }
    return "Fcat@$year$month$day"
}

fun httpBuildQuery(array: Map<String, String?>): String? {
    var reString: String? = ""
    //遍历数组形成akey=avalue&bkey=bvalue&ckey=cvalue形式的的字符串
    val it = array.entries.iterator()
    while (it.hasNext()) {
        val entry = it.next() as kotlin.collections.Map.Entry<*, *>
        val key = entry.key
        val value = entry.value
        reString += "$key=$value&"
    }
    reString = reString!!.substring(0, reString.length - 1)
    //将得到的字符串进行处理得到目标格式的字符串
    try {
        reString = java.net.URLEncoder.encode(reString, "utf-8")
    } catch (e: UnsupportedEncodingException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }
    reString = reString!!.replace("%3D", "=").replace("%26", "&")
    return reString
}


fun getCommonRequestMap(): TreeMap<String, String> {
    val commonParamsMap = TreeMap<String, String>()
    commonParamsMap["identify"] = (Build.FINGERPRINT + "/" + getMac()).md5Encode()
    commonParamsMap["platform"] = "ANDROID"
    commonParamsMap["version"] = BaseApplication.get().versionName()
    commonParamsMap["ts"] = (System.currentTimeMillis() / 1000).toString()
    return commonParamsMap
}