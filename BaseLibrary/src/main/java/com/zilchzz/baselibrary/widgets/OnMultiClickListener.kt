package com.zilchzz.baselibrary.widgets

import android.view.View

/**
 * 防多次点击
 * Created by cy on 2018/7/11.
 */
abstract class OnMultiClickListener : View.OnClickListener {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private val MIN_CLICK_DELAY_TIME = 1000
    private var lastClickTime: Long = 0

    abstract fun onMultiClick(v: View)

    override fun onClick(v: View) {
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime
            onMultiClick(v)
        }
    }
}