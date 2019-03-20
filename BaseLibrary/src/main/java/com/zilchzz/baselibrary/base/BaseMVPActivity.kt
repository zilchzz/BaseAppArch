package com.zilchzz.baselibrary.base

import android.os.Bundle
import com.zilchzz.baselibrary.net.OnHttpError
import com.zilchzz.baselibrary.widgets.ProgressLoading
import com.zilchzz.baselibrary.widgets.bindDialog
import com.zilchzz.baselibrary.net.HttpCode
import java.lang.reflect.ParameterizedType

/**
 * Created by zhongzhanzhong on 2018/3/9.
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseMVPActivity<V : BaseView, P, out T : BasePresenter<V, P>> : BaseActivity(), OnHttpError {
    protected val mPresenter: T? by lazy {
        val clazz: Class<T>? = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[2]
                as? Class<T>
        try {
            val t = clazz?.newInstance()
            t?.mBaseView = this@BaseMVPActivity as V
            t
        } catch (e: Throwable) {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (initBaseView() != null) {
            mPresenter?.mBaseView = initBaseView()!!
        }
    }

    //用来弹错误框的dialog
    private val errorDialog by bindDialog {
        setCancelable(false)
        negativeButton(false)
        positiveButton(true, onPositiveButtonClicked = {
            dismiss()
        })
    }


    //等需要使用的时候才创建这个dialog
    private val mLoadingDialog: ProgressLoading by lazy {
        ProgressLoading.create(this)
    }


    /**
     * 统计处理网络错误的情况
     */
    override fun onError(httpCode: HttpCode, code: Int?, error: String?) {
        mLoadingDialog.hideLoading()
        /*when (code) {
            801 -> {//登录超时
                LoginStatus.loginOut()
                toast("登录超时,请重新登录")
                AppManager.instance.finishAllActivities()
            }
            803 -> {//refresh_token过期
                pop()
            }
            else -> {
                errorDialog.message(error ?: "未知错误")
                errorDialog.show()
            }
        }*/
        errorDialog.message(error ?: "未知错误")
    }

    //如果presenter中的baseView不是这个activity的话，覆盖这个方法，返回真实的BaseView
    open fun initBaseView(): V? = null
}