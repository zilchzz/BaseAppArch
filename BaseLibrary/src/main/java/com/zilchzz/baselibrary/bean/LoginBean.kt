package com.zilchzz.baselibrary.bean

/**
 * 登录结果返回
 * Created by cy on 2018/7/11.
 */
data class LoginBean(
        var hasLogin: Boolean = false,//是否登录
        val sid: String?,// sid
        val phone: String?,// 手机号
        val member_id: String?// 用户id
)