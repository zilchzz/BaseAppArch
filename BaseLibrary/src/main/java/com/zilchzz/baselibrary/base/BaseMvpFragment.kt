package com.zilchzz.baselibrary.base

import android.os.Bundle
import com.zilchzz.baselibrary.net.HttpCode
import com.zilchzz.baselibrary.net.OnHttpError
import com.zilchzz.baselibrary.widgets.ProgressLoading
import com.zilchzz.baselibrary.widgets.bindDialog
import java.lang.reflect.ParameterizedType

/**
 * Created by zhongzhanzhong on 2018/3/9.
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseMvpFragment<V : BaseView, P, out T : BasePresenter<V, P>> : BaseFragment(), BaseView, OnHttpError {

    protected var offset = 0
    protected val PAGER_SIZE = "20"

    protected val mPresenter: T? by lazy {
        val clazz: Class<T>? = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[2]
                as? Class<T>
        try {
            val t = clazz?.newInstance()
            t?.mBaseView = this@BaseMvpFragment as V
            t
        } catch (e: Throwable) {
            null
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (initBaseView() != null) {
            mPresenter?.mBaseView = initBaseView()!!
        }
    }

    //如果presenter中的baseView不是这个activity的话，覆盖这个方法，返回真实的BaseView
    open fun initBaseView(): V? = null

    //用来弹错误框的dialog
    protected val errorDialog by bindDialog {
        setCancelable(false)
        negativeButton(false)
        positiveButton(true, onPositiveButtonClicked = {
            dismiss()
        })
    }


    //等需要使用的时候才创建这个dialog
    private val mLoadingDialog: ProgressLoading by lazy {
        ProgressLoading.create(requireActivity())
    }

    override fun onError(httpCode: HttpCode, code: Int?, error: String?) {
        mLoadingDialog.hideLoading()
        /*when (code) {
            801 -> {//登录超时
                LoginStatus.loginOut()
                toast("登录超时,请重新登录")
                AppManager.instance.finishAllActivities()
            }
            802 -> {//其他手机登录
                forceLoginHandler(error)
            }
            803 -> {//refresh_token过期
                pop()
            }
            1007 -> {

            }
            else -> {
                errorDialog.message(error ?: "未知错误")
                errorDialog.show()
            }
        }*/
        errorDialog.message(error ?: "未知错误")
    }

    override fun onRequestStarted() {
        mLoadingDialog.showLoading()
    }

    override fun onRequestCompleted() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.hideLoading()
        }
    }
}
