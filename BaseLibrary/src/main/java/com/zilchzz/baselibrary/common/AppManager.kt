package com.zilchzz.baselibrary.common

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import java.util.*

/**
 * Created by ouyangfeng on 2018/3/15.
 */
class AppManager private constructor() {
    private val activityStack: Stack<Activity> = Stack()

    companion object {
        val instance: AppManager by lazy { AppManager() }
    }

    /**
     * 退出APP
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    fun exitApp(context: Context) {
        finishAllActivities(true)
        val activityManager = context.getSystemService(
                Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.killBackgroundProcesses(context.packageName)
    }

    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    fun finishActivity(activity: Activity) {
        activity.finish()
        activityStack.remove(activity)
    }

    fun currentActivity(): Activity {
        return activityStack.lastElement()
    }

    /**
     * remove activity，注册完后，跳转登陆界面时，将之前的activity remove掉
     * @param removeAll remove all activities
     */
    public fun finishAllActivities(removeAll: Boolean = false) {
        val startIndex: Int = if (removeAll) 0 else 1
        for (activityIndex in startIndex until activityStack.size) {
            activityStack[activityIndex].finish()
        }
    }
}