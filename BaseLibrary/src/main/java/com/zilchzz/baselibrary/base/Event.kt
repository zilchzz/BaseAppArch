package com.zilchzz.baselibrary.base

data class Event(var type: Int, var extra: Any? = null) {
    companion object {
        /**
         * 登录成功
         */
        val TYPE_LOGIN_SUCCESS = 1
        /**
         * 退出登录
         */
        val TYPE_LOGOUT_SUCCESS = 2

        /**
         * 锁定屏幕后解锁成功
         */
        val TYPE_UNLOCK_SUCCESS = 3

        /**
         * 添加或者删除自选
         */
        val TYPE_OPTIONAL_CHANGED = 4

        /**
         * 微信分享，成功/失败
         */
        val TYPE_WECHAT_SHARE_SUCC = 5
        val TYPE_WECHAT_SHARE_FAIL = 6
        val TYPE_WECHAT_SHARE_DENIED = 7

        /**
         * 登录
         */
        val TYPE_LOGIN = 8

        /**
         * 首页资产同步
         */
        val TYPE_SYNC = 9

        /**
         * 切换到自选tab
         */
        val TYPE_CHECK_OPTIONAL = 10
    }
}