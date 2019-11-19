package com.zilchzz.baselibrary.base

/**
 * Created by ouyangfeng on 2018/3/9.
 */
interface BaseView {
    /**
     * 网络接口请求开始，用于在UI页面展示等待对话框等操作
     */
    fun onRequestStarted() {}

    /**
     * 网络接口请求完成，对应[onRequestStarted],在请求完成后调用
     */
    fun onRequestCompleted() {}
}