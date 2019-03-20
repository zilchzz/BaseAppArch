package com.zilchzz.baselibrary.widgets

import android.content.Context
import android.support.v4.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 使用代理方式创建对话框
 *
 * @param init 初始化函数
 */
fun Fragment.bindDialog(init: (Dialog.() -> Unit)? = null):
        DialogLazyBinder<Fragment> = buildDialog(init)

/**
 * 使用代理方式创建对话框
 *
 * @param init 初始化函数
 */
fun Context.bindDialog(init: (Dialog.() -> Unit)? = null):
        DialogLazyBinder<Context> = buildDialog(init)

fun <T> buildDialog(init: (Dialog.() -> Unit)? = null) = DialogLazyBinder<T>(init)

class DialogLazyBinder<T>(private var initialize: (Dialog.() -> Unit)? = null) :
        ReadOnlyProperty<T, Dialog> {
    private var mDialog: Dialog? = null
    override fun getValue(thisRef: T, property: KProperty<*>): Dialog {
        if (null == mDialog) {
            if (thisRef is Context) {
                mDialog = Dialog(thisRef)
            }

            if (thisRef is Fragment) {
                mDialog = Dialog(thisRef.requireActivity())
            }

            if (thisRef is android.app.Dialog) {
                mDialog = Dialog(thisRef.context)
            }

            mDialog!!.setCanceledOnTouchOutside(false)
            initialize?.invoke(mDialog!!)
        }
        return mDialog!!
    }

}
