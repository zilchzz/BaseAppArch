package com.zilchzz.baselibrary.annotation

import java.lang.annotation.Inherited

/**
 * 在某些情况下，Fragment需要自己处理按钮的回退事件，这个注解就是为了解决这个问题而产生的。
 * 使用方法很简单，在当前Fragment类上添加注解[@InterceptBackPressed(true)]表示要拦截回退事件。
 * 这个时候，在按下回退按钮的时候，系统会自动调用BaseFragment中的onBackPressed方法。
 * 因此，你只需要重写onBackPressed方法完成自定义的回退逻辑即可。
 *
 * @author zhongzhanzhong 2018-03-12 01:07
 */
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class InterceptBackPressed(val value: Boolean = false)