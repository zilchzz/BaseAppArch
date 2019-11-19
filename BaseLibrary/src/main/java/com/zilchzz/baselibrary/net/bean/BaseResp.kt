package com.zilchzz.baselibrary.net.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by ouyangfeng on 2018/3/11.
 */
class BaseResp<T>(@SerializedName(value = "code", alternate = ["errcode"]) val code: Int,@SerializedName(value = "message", alternate = ["msg"])  val message: String, @SerializedName(value = "data", alternate = ["datalist"]) val data: T) {
    companion object {
        val CODE_SUCCESS = 0
        val CODE_JSON_PARSE_ERR = -2
        val CODE_USER_KNOWN = 20024
        val CODE_SESSION_EXPIRED = 9998
        val CODE_ACCOUNT_EXCEPTION = 7777
    }
}