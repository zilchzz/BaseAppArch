package com.zilchzz.baselibrary.base

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.AnimRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.zilchzz.baselibrary.BaseApplication
import com.zilchzz.baselibrary.R
import com.zilchzz.baselibrary.annotation.Toolbar
import com.zilchzz.baselibrary.annotation.ToolbarMode
import com.zilchzz.baselibrary.annotation.ToolbarSetting
import com.zilchzz.baselibrary.common.AppManager
import com.zilchzz.baselibrary.exception.NoContainerIdException
import com.zilchzz.baselibrary.ext.hideKeyboard
import com.zilchzz.baselibrary.ext.isActive
import com.zilchzz.baselibrary.utils.ISLog
import com.zilchzz.baselibrary.utils.LoginStatus
import com.zilchzz.baselibrary.widgets.AbsToolbar
import com.zilchzz.baselibrary.widgets.DefaultToolbar
import com.zilchzz.baselibrary.widgets.bindDialog
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.bundleOf
import java.io.Serializable
import kotlin.reflect.KClass

open class BaseActivity : RxAppCompatActivity() {
    private lateinit var mContentView: LinearLayout
    protected var mToolbar: AbsToolbar? = null
    private var mCurrentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus()
        super.onCreate(savedInstanceState)
        AppManager.instance.addActivity(this)
        prepare()
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        val contentView = layoutInflater.inflate(layoutResID, mContentView, false)
        setContentView(contentView)
    }

    override fun setContentView(view: View) {
        setContentView(view, view.layoutParams)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        var lp = params
        if (null == lp) lp = generateDefaultLayoutParams()
        mContentView.removeAllViews()
        addToolbarToContentView()
        initToolbarSetting()
        mContentView.addView(view, lp)
    }

    private fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }

    // 读取注解Toolbar设置
    private fun initToolbarSetting() {
        val toolbarAnno = this.javaClass.annotations.find { it is ToolbarSetting } as? ToolbarSetting
        if (null != toolbarAnno) {
            mToolbar?.displayNavigationIcon(toolbarAnno.displayNavigationIcon)

            if (!TextUtils.isEmpty(toolbarAnno.title)) {
                mToolbar?.title = toolbarAnno.title
            }

            if (toolbarAnno.titleRes > 0) {
                mToolbar?.title = resources.getString(toolbarAnno.titleRes)
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

    private fun prepare() {
        mContentView = LinearLayout(this)
        mContentView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mContentView.orientation = LinearLayout.VERTICAL
        mContentView.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.windowBackground))
        super.setContentView(mContentView)
    }

    /**
     *  将toolBar加到当前布局
     */
    private fun addToolbarToContentView() {
        mToolbar = toolbar()

        if (null != mToolbar) {
            var layoutParams = mToolbar!!.layoutParams
            if (null == layoutParams) {
                layoutParams = generateDefaultLayoutParams()
            }

            mContentView.addView(mToolbar, 0, layoutParams)
        }
    }

    override fun setTitle(title: CharSequence?) {
        if (null == mToolbar) {
            super.setTitle(title)
        } else {
            mToolbar?.title = title
        }
    }

    /**
     * 根据注解去生成一个toolBar
     */
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
                    mToolbar = layoutInflater.inflate(toolbarAnno.layout, mContentView, false)
                            as? AbsToolbar
                }

                if (null == mToolbar && toolbarAnno.entityClass != AbsToolbar::class) {
                    val constructor = toolbarAnno.entityClass.java.getDeclaredConstructor(Context::class.java)
                        ?: throw RuntimeException(
                            toolbarAnno.entityClass.java.simpleName +
                                    "必须需要提供一个参数为${Context::class.java.simpleName}的构造方法"
                        )
                    mToolbar = constructor.newInstance(this) as AbsToolbar
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
     * 加载默认toolBar
     */
    private fun userDefaultToolbar(): DefaultToolbar {
        return layoutInflater.inflate(R.layout.default_toolbar, mContentView, false) as DefaultToolbar
    }

    fun hasToolbar(): Boolean {
        return null != mToolbar
    }

    /**
     * 从Intent中获取数据
     *
     * @param init 初始化函数
     */
    fun intent(init: Intent.() -> Unit) {
        intent.init()
    }

    fun push(
        @AnimRes enter: Int = R.anim.fragment_enter,
        @AnimRes exit: Int = R.anim.fragment_exit,
        @AnimRes popEnter: Int = R.anim.fragment_pop_enter,
        @AnimRes popExit: Int = R.anim.fragment_pop_exit,
        fragment: KClass<out BaseFragment>,
        requestCode: Int = BaseFragment.REQUEST_NOTHING,
        vararg params: Pair<String, Any>
    ) {
        val fragmentName = fragment.java.name
        if (fragmentName == mCurrentFragmentTag) return

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(enter, exit, popEnter, popExit)
        if (!TextUtils.isEmpty(mCurrentFragmentTag)) {
            val currentFragment = fragmentManager.findFragmentByTag(mCurrentFragmentTag)

            if (null != currentFragment) {
                transaction.hide(currentFragment)
                transaction.addToBackStack(mCurrentFragmentTag)
            }
        }

        var targetFragment = fragmentManager.findFragmentByTag(fragmentName) as? BaseFragment
        if (null != targetFragment && targetFragment.isActive()) {
            targetFragment.arguments?.clear()
            targetFragment.arguments?.putAll(bundleOf(*params))
            transaction.show(targetFragment)
        } else {
            targetFragment = Fragment.instantiate(
                this, fragmentName,
                bundleOf(*params)
            ) as BaseFragment?
            if (null != targetFragment) {
                transaction.add(containerId(), targetFragment, fragmentName)
            }
        }

        if (requestCode != BaseFragment.REQUEST_NOTHING)
            targetFragment?.setRequestCode(requestCode)

        transaction.commitAllowingStateLoss()

        mCurrentFragmentTag = fragmentName
    }

    /**
     * 开启一个fragment页面,当希望传递一个requestCode至目标页面时，请传递一个除[BaseFragment.REQUEST_NOTHING]的值
     */
    fun push(fragment: KClass<out BaseFragment>, requestCode: Int, vararg params: Pair<String, Any>) {
        push(
            enter = R.anim.fragment_enter,
            exit = R.anim.fragment_exit,
            popEnter = R.anim.fragment_pop_enter,
            popExit = R.anim.fragment_pop_exit,
            fragment = fragment,
            requestCode = requestCode,
            params = * params
        )
    }

    fun popForResult(requestCode: Int, resultCode: Int, data: Bundle?): Boolean {
        val fragmentManager = supportFragmentManager

        var fragmentTag: String? = null
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentTag = fragmentManager
                .getBackStackEntryAt(fragmentManager.backStackEntryCount - 1)?.name
        }

        val lastFragment = supportFragmentManager.findFragmentByTag(mCurrentFragmentTag)
                as? BaseFragment

        if (fragmentManager.popBackStackImmediate()) {
            mCurrentFragmentTag = fragmentTag
            val targetFragment = supportFragmentManager.findFragmentByTag(mCurrentFragmentTag)
                    as? BaseFragment
            targetFragment?.onBackCompleted(lastFragment)
            targetFragment?.onFragmentResult(requestCode, resultCode, data)
            return true
        }

        val intent = Intent()
        fullDataIntoIntent(intent, data)
        setResult(resultCode)
        finish()
        return false
    }

    /**
     * 无缝切换到Fragment，不展示切换动画
     *
     * @param fragment 切换的目标Fragment
     * @param smooth true 平行切换Fragment，不加入回退栈。
     *               false 正确切换Fragment，并加入回退栈
     * @param params 传递到目标Fragment的数据 (eg："name" to "zhongzhanzhong")
     */
    fun switchTo(
        fragment: KClass<out BaseFragment>,
        smooth: Boolean = false,
        vararg params: Pair<String, Any>
    ) {
        hideKeyboard()
        if (!smooth) {
            push(
                enter = 0,
                exit = 0,
                popEnter = 0,
                popExit = 0,
                fragment = fragment,
                params = *params
            )
        } else {
            val success = SmoothFragmentManager.get(this)
                .switchTo(
                    containerId = containerId(),
                    fragment = fragment,
                    params = *params
                )
            if (success) {
                mCurrentFragmentTag = fragment.java.name
            }
        }
    }

    /**
     * 平行切换Fragment，通常用于Tab页面切换Fragment
     *
     * @param fragment 目标Fragment
     * @param params 传递到目标Fragment的数据 (eg："name" to "zhongzhanzhong")
     */
    fun smoothSwitchTo(
        fragment: KClass<out BaseFragment>,
        vararg params: Pair<String, Any>
    ) {
        switchTo(fragment, true, *params)
    }

    fun pop(): Boolean {
        val fragmentManager = supportFragmentManager

        var fragmentTag: String? = null
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentTag = fragmentManager
                .getBackStackEntryAt(fragmentManager.backStackEntryCount - 1)?.name
        }

        val lastFragment = supportFragmentManager.findFragmentByTag(mCurrentFragmentTag)
                as? BaseFragment

        if (fragmentManager.popBackStackImmediate()) {
            mCurrentFragmentTag = fragmentTag
            (supportFragmentManager.findFragmentByTag(mCurrentFragmentTag) as? BaseFragment)
                ?.onBackCompleted(lastFragment)
            return true
        }

        finish()
        return false
    }

    /**
     * 获取栈顶的Fragment
     *
     * @return 处于栈顶的Fragment
     */
    private fun getFragmentInStackTop(): BaseFragment? {
        try {
            return supportFragmentManager.findFragmentById(containerId()) as? BaseFragment
        } catch (e: Exception) {
        }
        return null
    }

    /**
     * 回退事件处理逻辑：如果Fragment拦截了回退事件，则将事件交给Fragment处理；否则，Activity自行处理
     */
    override fun onBackPressed() {
        hideKeyboard()
        if (getFragmentInStackTop()?.backPressedIsIntercepted() == true) {
            getFragmentInStackTop()?.onBackPressed()
            ISLog.e("${getFragmentInStackTop()} 拦截了回退事件")
        } else {
            pop()
        }
    }

    fun popToFragment(fragment: KClass<out BaseFragment>): Boolean {
        val lastFragment = supportFragmentManager.findFragmentByTag(mCurrentFragmentTag)
                as? BaseFragment
        if (supportFragmentManager.popBackStackImmediate(
                fragment.java.name,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        ) {
            mCurrentFragmentTag = fragment.java.name
            (supportFragmentManager.findFragmentByTag(mCurrentFragmentTag) as? BaseFragment)
                ?.onBackCompleted(lastFragment)
            return true
        }
        return false
    }

    private fun fullDataIntoIntent(intent: Intent, data: Bundle?) {
        if (null != data) {
            val keySet = data.keySet()
            val iterator = keySet.iterator()

            while (iterator.hasNext()) {
                val key = iterator.next()
                val value = data.get(key)
                when (value) {
                    is Boolean -> intent.putExtra(key, value)
                    is Byte -> intent.putExtra(key, value)
                    is Char -> intent.putExtra(key, value)
                    is Short -> intent.putExtra(key, value)
                    is Int -> intent.putExtra(key, value)
                    is Long -> intent.putExtra(key, value)
                    is Float -> intent.putExtra(key, value)
                    is Double -> intent.putExtra(key, value)
                    is String -> intent.putExtra(key, value)
                    is CharSequence -> intent.putExtra(key, value)
                    is Parcelable -> intent.putExtra(key, value)
                    is Serializable -> intent.putExtra(key, value)
                    is BooleanArray -> intent.putExtra(key, value)
                    is ByteArray -> intent.putExtra(key, value)
                    is CharArray -> intent.putExtra(key, value)
                    is DoubleArray -> intent.putExtra(key, value)
                    is FloatArray -> intent.putExtra(key, value)
                    is IntArray -> intent.putExtra(key, value)
                    is LongArray -> intent.putExtra(key, value)
                    is Array<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        when {
                            value.isArrayOf<Parcelable>() -> intent.putExtra(key, value)
                            value.isArrayOf<CharSequence>() -> intent.putExtra(key, value)
                            value.isArrayOf<String>() -> intent.putExtra(key, value)
                            else -> throw RuntimeException("Unsupported bundle component (${value.javaClass})")
                        }
                    }
                    is ShortArray -> intent.putExtra(key, value)
                    is Bundle -> intent.putExtra(key, value)
                    else -> throw RuntimeException("Unsupported bundle component (${value.javaClass})")
                }
            }
        }
    }

    open fun containerId(): Int {
        throw NoContainerIdException("请重写该方法，返回Fragment的容器Id")
    }

    override fun onDestroy() {
        super.onDestroy()
//        unRegisterEventBus()
        AppManager.instance.finishActivity(this)
        SmoothFragmentManager.recycle(this)
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
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * 设置状态栏
     */
    protected open fun setStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(BaseApplication.get(), R.color.status_color)
        }
    }

}