package com.zilchzz.baselibrary.widgets

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zilchzz.baselibrary.R
import com.zilchzz.baselibrary.ext.onClick
import com.zilchzz.baselibrary.ext.onSingleClick
import org.jetbrains.anko.dip
import kotlin.properties.Delegates

/**
 * Default toolbar
 *
 * @author ouyangfeng 2016-12-11 14:14
 */
open class DefaultToolbar(context: Context, attrs: AttributeSet?) : AbsToolbar(context, attrs) {
    private var mTitleView: TextView by Delegates.notNull()
    private var mNaviButton: TextView by Delegates.notNull()
    var mRightTextView: TextView by Delegates.notNull()

    constructor(context: Context) : this(context, null)

    init {
        setContentInsetsAbsolute(0, 0)

        mTitleView = TextView(context)
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        mTitleView.layoutParams = lp
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17.0f)
        mTitleView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite))
        addView(mTitleView)

        mNaviButton = TextView(context)
        mNaviButton.isClickable = true
        mNaviButton.layoutParams = Toolbar.LayoutParams(dip(60), ViewGroup.LayoutParams.MATCH_PARENT)
        mNaviButton.setPadding(dip(20), 0, 0, 0)
        mNaviButton.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.app_icon_back, 0, 0, 0)
        addView(mNaviButton)

        mRightTextView = TextView(context)
        mRightTextView.isClickable = true
        mRightTextView.visibility = View.GONE
        val rightParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        rightParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        mRightTextView.layoutParams = rightParams
        mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17.0f)
        mRightTextView.setPadding(0, 0, dip(20), 0)
        addView(mRightTextView)

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            this.elevation = 4f
        }
        mNaviButton.onSingleClick { onNavigationClick?.invoke(this@DefaultToolbar) }
    }

    override fun setNavigationDrawable(drawableId: Int) {
        mNaviButton.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0)
    }

    override fun displayNavigationIcon(display: Boolean) {
        mNaviButton.visibility = if (display) View.VISIBLE else View.GONE
    }

    override fun setTitle(resId: Int) {
        mTitleView.setText(resId)
    }

    override fun setTitle(title: CharSequence?) {
        mTitleView.text = title
    }

    override fun setTitleColor(color: Int) {
        mTitleView.setTextColor(color)
    }

    override fun getTitle(): String = mTitleView.text.toString()

    /**
     * 设置标题右边文字点击事件
     */
    fun setRightTextListener(listener: OnClickListener) {
        mRightTextView.onClick(listener)
    }

    fun setRightText(rightText: String) {
        mRightTextView.text = rightText
        mRightTextView.visibility = View.VISIBLE
    }

    fun setRightTextColor(rightTextColor: Int) {
        mRightTextView.setTextColor(context.resources.getColor(rightTextColor))
    }

    fun setRightIcon(resInt: Int) {
        mRightTextView.setCompoundDrawablesWithIntrinsicBounds(resInt, 0, 0, 0)
        mRightTextView.visibility = View.VISIBLE
    }
}
