package com.zilchzz.baselibrary.base

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.AnimRes
import android.support.annotation.StringRes
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.trello.rxlifecycle2.components.support.RxFragment
import com.zilchzz.baselibrary.R
import com.zilchzz.baselibrary.annotation.InterceptBackPressed
import com.zilchzz.baselibrary.annotation.Toolbar
import com.zilchzz.baselibrary.annotation.ToolbarMode
import com.zilchzz.baselibrary.annotation.ToolbarSetting
import com.zilchzz.baselibrary.ext.hideKeyboard
import com.zilchzz.baselibrary.utils.LoginStatus
import com.zilchzz.baselibrary.widgets.AbsToolbar
import com.zilchzz.baselibrary.widgets.DefaultToolbar
import org.greenrobot.eventbus.EventBus
import kotlin.reflect.KClass

abstract class BaseFragment : RxFragment() {
    companion object {
        const val RESULT_OK = Activity.RESULT_OK
        const val RESULT_CANCEL = Activity.RESULT_CANCELED
        const val REQUEST_NOTHING = 0XF12345 //注意，使用这个默认值，表示不发起请求
    }

    private var hasInit = false
    private var mRequestCode = -1
    private var mContentView: LinearLayout? = null
    protected var mToolbar: AbsToolbar? = null
    private var mResultCode = RESULT_CANCEL
    private var mResultData: Bundle? = null
    private val TAG = javaClass.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentView = LinearLayout(activity)
        mContentView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT)
        mContentView!!.orientation = LinearLayout.VERTICAL
        mToolbar = toolbar()
        if (null != mToolbar) {
            if ((activity as? BaseActivity)?.hasToolbar() == true) {
                Log.e(javaClass.name, "${activity?.javaClass?.name} 已经添加了Toolbar，" +
                        "${javaClass.name}也添加Toolbar将导致Toolbar重复显示")
            }

            mToolbar!!.onNavigationClick = {
                activity?.onBackPressed()
            }
            mContentView!!.removeView(mToolbar)
            mContentView!!.addView(mToolbar, 0)
        }
        val bindView = onBindView(inflater, container, savedInstanceState)
        if (null != bindView) mContentView!!.addView(bindView)
        initToolbarSetting()
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInit) {
            hasInit = true
            onViewCreatedCallBack()
        }
    }

    /**
     * 初始化页面
     */
    protected open fun onViewCreatedCallBack() {}

    abstract fun onBindView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View?

    // 读取注解Toolbar设置
    private fun initToolbarSetting() {
        val toolbarAnno = this.javaClass.annotations.find { it is ToolbarSetting } as? ToolbarSetting
        if (null != toolbarAnno) {
            mToolbar?.displayNavigationIcon(toolbarAnno.displayNavigationIcon)

            if (!TextUtils.isEmpty(toolbarAnno.title)) {
                setTitle(toolbarAnno.title)
            }

            if (toolbarAnno.titleRes > 0) {
                setTitle(toolbarAnno.titleRes)
            }

            if (toolbarAnno.navigationDrawable > 0) {
                mToolbar?.setNavigationDrawable(toolbarAnno.navigationDrawable)
            }

            if (mToolbar != null && mToolbar is DefaultToolbar) {
                val defaultToolbar = mToolbar as DefaultToolbar
                if (toolbarAnno.rightTextRes > 0) {
                    defaultToolbar.mRightTextView.visibility = View.VISIBLE
                    defaultToolbar.mRightTextView.setBackgroundResource(toolbarAnno.rightTextRes)
                }
                if (!TextUtils.isEmpty(toolbarAnno.rightText)) {
                    defaultToolbar.mRightTextView.visibility = View.VISIBLE
                    defaultToolbar.mRightTextView.text = toolbarAnno.rightText
                }
                defaultToolbar.mRightTextView.setTextColor(Color.parseColor(toolbarAnno.rightTextColor))
            }

            mToolbar?.setTitleColor(Color.parseColor(toolbarAnno.titleColor))
            mToolbar?.setBackgroundColor(Color.parseColor(toolbarAnno.backgroundColor))
        }
    }

    open fun setTitle(title: String?) {
        if (null != mToolbar) {
            mToolbar?.title = title
        } else {
            activity?.title = title
        }
    }

    open fun setTitle(@StringRes resId: Int) {
        setTitle(getString(resId))
    }

    open fun toolbar(): AbsToolbar? {
        if (null == mToolbar) {
            // 优先使用注解生成Toolbar
            val toolbarAnno = this.javaClass.annotations.find { it is Toolbar } as? Toolbar
            if (null != toolbarAnno) {
                if (ToolbarMode.NONE === toolbarAnno.mode) return null

                if (toolbarAnno.layout == -1 && toolbarAnno.entityClass == AbsToolbar::class) {
                    throw RuntimeException("使用自定义布局，layout和entityClass至少需要提供一个")
                }

                if (toolbarAnno.layout > 0) {
                    mToolbar = layoutInflater().inflate(toolbarAnno.layout, mContentView, false)
                            as? AbsToolbar
                }

                if (null == mToolbar && toolbarAnno.entityClass != AbsToolbar::class) {
                    val toolBarConstructor = toolbarAnno.entityClass.java.getConstructor(Context::class.java)
                            ?: throw RuntimeException(toolbarAnno.entityClass::class.java.simpleName +
                                    "必须需要提供一个参数为${Context::class.java.simpleName}的构造方法")
                    mToolbar = toolBarConstructor.newInstance(requireActivity())
                }
            }

            // Toolbar依然未完成赋值，则使用默认Toolbar
            if (null == mToolbar) {
                mToolbar = userDefaultToolbar()
            }

            mToolbar?.onNavigationClick = {
                onBackPressed()
            }
        }

        return mToolbar
    }

    protected fun userDefaultToolbar(): DefaultToolbar {
        return layoutInflater().inflate(R.layout.default_toolbar, mContentView, false) as DefaultToolbar
    }

    /**
     * 获取LayoutInflater实例
     *
     * @return [LayoutInflater]实例
     */
    private fun layoutInflater(): LayoutInflater = LayoutInflater.from(activity)

    fun push(@AnimRes enter: Int = R.anim.fragment_enter,
             @AnimRes exit: Int = R.anim.fragment_exit,
             @AnimRes popEnter: Int = R.anim.fragment_pop_enter,
             @AnimRes popExit: Int = R.anim.fragment_pop_exit,
             fragment: KClass<out BaseFragment>,
             vararg params: Pair<String, Any>,
             requestCode: Int = REQUEST_NOTHING
    ) {
        (activity as? BaseActivity)?.push(enter, exit, popEnter, popExit, fragment, requestCode, *params)
    }

    fun push(fragment: KClass<out BaseFragment>, vararg params: Pair<String, Any>, requestCode: Int = REQUEST_NOTHING) {
        push(enter = R.anim.fragment_enter,
                exit = R.anim.fragment_exit,
                popEnter = R.anim.fragment_pop_enter,
                popExit = R.anim.fragment_pop_exit,
                fragment = fragment,
                requestCode = requestCode,
                params = * params)
    }

    fun pop() {
        (activity as? BaseActivity)?.pop()
    }

    /**
     * 回退至指定的Fragment
     *
     * @param fragment 目标Fragment
     */
    fun popToFragment(fragment: KClass<out BaseFragment>) {
        (activity as? BaseActivity)?.popToFragment(fragment = fragment)
    }

    fun switchTo(smooth: Boolean = false,
                 fragment: KClass<out BaseFragment>,
                 vararg params: Pair<String, Any>) {
        (activity as? BaseActivity)?.switchTo(fragment, smooth, *params)
    }

    fun data(f: Bundle.() -> Unit) {
        arguments?.f()
    }

    fun setRequestCode(requestCode: Int) {
        mRequestCode = requestCode
    }

    fun backPressedIsIntercepted(): Boolean {
        val interceptBackPressedAnno = this::class.java.annotations.find { it is InterceptBackPressed }
                as? InterceptBackPressed
        return interceptBackPressedAnno?.value ?: false
    }

    fun setResult(resultCode: Int, resultData: Bundle? = null) {
        mResultCode = resultCode
        mResultData = resultData
    }

    fun startFragmentForResult(fragment: KClass<out BaseFragment>,
                               requestCode: Int,
                               vararg data: Pair<String, Any>) {
        (activity as? BaseActivity)?.push(fragment = fragment,
                requestCode = requestCode,
                params = *data)
    }

    open fun onBackPressed() {

    }

    open fun finish() {
        (activity as? BaseActivity)?.popForResult(requestCode = mRequestCode,
                resultCode = mResultCode,
                data = mResultData)
    }

    /**
     * 回退到当前页面操作完成后会调用这个方法
     *
     * @param from 表示源头Fragment
     */
    open fun onBackCompleted(from: BaseFragment? = null) {

    }

    open fun onFragmentResult(requestCode: Int,
                              resultCode: Int,
                              data: Bundle?) {
    }

    /**
     * 设置标题右边的文字点击事件
     */
    fun setRightTextListener(listener: View.OnClickListener) {
        if (mToolbar != null && mToolbar is DefaultToolbar) {
            val defaultToolbar = mToolbar as DefaultToolbar
            defaultToolbar.setRightTextListener(listener)
        }
    }

    /**
     * 设置标题右边的文字
     */
    fun setDefaultToolbarTitle(title: String) {
        if (mToolbar != null && mToolbar is DefaultToolbar) {
            val defaultToolbar = mToolbar as DefaultToolbar
            defaultToolbar.title = title
        }
    }

    /**
     * 隐藏键盘
     */
    fun hideKeyboard() {
        if (activity != null && activity is BaseActivity) {
            val baseActivity = activity as BaseActivity
            baseActivity.hideKeyboard()
        }
    }

    /**
     * 注册EventBus
     */
    protected fun registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun unRegisterEventBus() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterEventBus()
    }

    protected fun ifLogin(method: () -> Unit?) {
        if (!LoginStatus.hasLogin) {
            EventBus.getDefault().post(Event(Event.TYPE_LOGIN))
        } else {
            method()
        }
    }

    open fun doubleClicked() {
    }
}