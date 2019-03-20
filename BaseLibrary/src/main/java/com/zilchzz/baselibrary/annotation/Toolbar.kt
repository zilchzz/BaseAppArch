package com.zilchzz.baselibrary.annotation

import android.support.annotation.LayoutRes
import com.zilchzz.baselibrary.widgets.AbsToolbar
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Toolbar(val mode: ToolbarMode,
                         @LayoutRes val layout: Int = -1,
                         val entityClass: KClass<out AbsToolbar> = AbsToolbar::class)

enum class ToolbarMode {
    /**
     * 不需要Toolbar
     */
    NONE,
    /**
     * 自定义Toolbar
     */
    CUSTOM
}