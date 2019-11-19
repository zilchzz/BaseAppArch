package com.zilchzz.baselibrary.base

import com.zilchzz.baselibrary.net.RetrofitHelper
import java.lang.reflect.ParameterizedType

/**
 * Created by ouyangfeng on 2018/3/9.
 */
open class BasePresenter<T : BaseView, P> {
    lateinit var mBaseView: T
    val mDynamicServer: P by lazy {
        val clazz: Class<P> = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<P>
        RetrofitHelper.getRetrofit().create(clazz)
    }
}