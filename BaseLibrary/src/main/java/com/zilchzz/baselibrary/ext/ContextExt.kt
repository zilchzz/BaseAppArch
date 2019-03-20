package com.zilchzz.baselibrary.ext

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.Fragment
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zilchzz.baselibrary.R
import io.reactivex.functions.Consumer
import org.jetbrains.anko.internals.AnkoInternals
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * 判断网络是否连接
 *
 * @return true 连接 false 断开
 */
fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return null != connectivityManager.activeNetworkInfo
}

/**
 * 获得当前app版本名称
 */
fun Context.versionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName
}

/**
 * 获取设备唯一标识符
 */
@SuppressLint("MissingPermission")
fun Context.getDeviceId(): String {
    val manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        manager.imei
    } else {
        manager.deviceId
    }
}

/**
 * 打开当前应用设置页面
 */
fun Context.openSystemSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName"))
    startActivity(intent)
}

/**
 * 隐藏软键盘
 */
fun Activity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}


/**
 * 判断当前Fragment是否处于活跃状态
 *
 * @return true 活跃状态 false 非活跃状态
 */
fun Fragment.isActive(): Boolean {
    return isAdded && !isDetached && !isRemoving
}


/**
 * 打开一个activity
 */
inline fun <reified T : Activity> Activity.startActivityNoAnim(vararg params: Pair<String, Any?>) {
    AnkoInternals.internalStartActivity(this, T::class.java, params)
}


/**
 * 打开一个activity，并且设置一个Activity的转场动画
 */
inline fun <reified T : Activity> Activity.startActivityWithAnim(vararg params: Pair<String, Any?>) {
    AnkoInternals.internalStartActivity(this, T::class.java, params)
    overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out)
}

/**
 * 打开一个activity
 * 并且设置一个Activity的转场动画
 * 并且返回结果
 */
inline fun <reified T : Activity> Activity.startActivityForResultWithAnim(requestCode: Int = 0, vararg params: Pair<String, Any?>) {
    AnkoInternals.internalStartActivityForResult(this, T::class.java, requestCode, params)
}

@SuppressLint("CheckResult")
fun Activity.requestReadPhoneStatePermissions(consumer: Consumer<Boolean>) {
    RxPermissions(this).request(Manifest.permission.READ_PHONE_STATE)
            .subscribe(consumer)
}


/**
 * 申请拍照跟读写内存权限
 */
@SuppressLint("CheckResult")
fun Activity.requestCameraAndDiskPermission(consumer: Consumer<Boolean>) {
    RxPermissions(this).request(Manifest.permission.CAMERA
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(consumer)
}

@SuppressLint("CheckResult")
fun Activity.requestStoragePermission(consumer: Consumer<Boolean>) {
    RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(consumer)
}

/**
 * 读取所有权限
 * 暂时是 电话和储存权限
 */
@SuppressLint("CheckResult")
fun Activity.requestAllPermissions(consumer: Consumer<Boolean>) {
    RxPermissions(this).request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE ,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(consumer)
}

private var mac = ""

fun getMac(): String {
    if (TextUtils.isEmpty(mac)) {
        mac = ""
        // 把当前机器上的访问网络接口的存入 Enumeration集合中
        var interfaces: Enumeration<NetworkInterface>? = null
        try {
            interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces!!.hasMoreElements()) {
                val netWork = interfaces.nextElement()
                // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
                val by = netWork.hardwareAddress
                if (by == null || by.size == 0) {
                    continue
                }
                val builder = StringBuilder()
                for (b in by) {
                    builder.append(String.format("%02X:", b))
                }
                if (builder.isNotEmpty()) {
                    builder.deleteCharAt(builder.length - 1)
                }
                val mac1 = builder.toString()
                // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
                if (netWork.name == "wlan0") {
                    mac = mac1
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
            mac = ""
        }

    }
    return mac
}







