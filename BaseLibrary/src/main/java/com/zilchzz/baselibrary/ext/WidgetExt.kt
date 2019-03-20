package com.zilchzz.baselibrary.ext

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.zilchzz.baselibrary.widgets.OnMultiClickListener
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern

/**
 * 判断一个EditText中的内容是否为一个手机号码
 */
fun EditText.isEditTextMobile(): Boolean = if (text.isNullOrEmpty()) {
    false
} else {
    val reg = "^1\\d{10}\$"
    Pattern.compile(reg).matcher(text.toString().replace("-", "")).matches()
}

/**
 * 判断一个EditText中的内容是否为数字
 */
fun EditText.isEditTextNumber(): Boolean = if (text.isNullOrEmpty()) {
    false
} else {
    val reg = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$"
    Pattern.compile(reg).matcher(text.toString()).matches()
}

/**
 * 判断一个EditText中内容是否正确的密码
 */
fun EditText.isEmpty(): Boolean = text.isNullOrEmpty()

/**
 * 判断一个EditText中内容是否正确的密码
 */
fun EditText.isEditTextPwd(): Boolean = if (text.isNullOrEmpty()) {
    false
} else {
    val reg = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,20}\$"
    Pattern.compile(reg).matcher(text).matches()
}

/**
 * 判断一个EditText中内容是否为0
 */
fun EditText.isEqualsZero(): Boolean = if (text.isNullOrEmpty()) {
    true
} else {
    try {
        text.toString().toDouble() == "0".toDouble()
    } catch (e: Exception) {
        true
    }
}

/**
 * MD5加密字符串
 * Created by cy on 2018/7/12.
 */
fun String.md5Encode(): String {
    if (this.isEmpty()) {
        return ""
    }
    try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
        val digest: ByteArray = instance.digest(toByteArray())//对字符串加密，返回字节数组
        val sb = StringBuffer()
        for (b in digest) {
            val i: Int = b.toInt() and 0xff//获取低八位有效值
            var hexString = Integer.toHexString(i)//将整数转化为16进制
            if (hexString.length < 2) {
                hexString = "0$hexString"//如果是一位的话，补0
            }
            sb.append(hexString)
        }
        return sb.toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * 获取密码
 * 返回的密码经过MD5加密
 */
fun EditText.getPassword(): String? {
    return text.toString().md5Encode()
}

/**
 * 显示软键盘
 */
fun View.showKeyboard() {
    doTask(300) {
        this.isFocusable = true
        this.requestFocus()
        val inputManager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        inputManager.showSoftInput(this, 0)
    }
}

/**
 *  扩展点击事件，参数为listener
 */
fun View.onClick(listener: View.OnClickListener): View {
    setOnClickListener(listener)
    return this
}

/**
 *  扩展点击事件，参数为方法
 */
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method() }
    return this
}

/**
 * 防止多次点击
 */
fun View.onSingleClick(method: () -> Unit): View {
    setOnClickListener(object : OnMultiClickListener() {
        override fun onMultiClick(v: View) {
            method()
        }
    })
    return this
}