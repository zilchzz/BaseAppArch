package com.zilchzz.baselibrary.widgets

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v7.widget.Toolbar
import android.util.AttributeSet

/**
 * Base class of all toolbar
 *
 * @author zhongzhanzhong 2016-12-11 12:10
 */
abstract class AbsToolbar(context: Context, attrs: AttributeSet?) : Toolbar(context , attrs) {
    var onNavigationClick: ((toolbar: AbsToolbar)->Unit)? = null

    constructor(context: Context): this(context , null)

    abstract fun displayNavigationIcon(display: Boolean)

    abstract fun setNavigationDrawable(drawableId: Int)

    abstract fun setTitleColor(@ColorInt color: Int)
}