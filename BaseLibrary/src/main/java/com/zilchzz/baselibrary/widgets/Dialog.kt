package com.zilchzz.baselibrary.widgets

import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.zilchzz.baselibrary.R
import com.zilchzz.baselibrary.ext.onSingleClick

/**
 * Dialog
 *
 * @author ouyangfeng 2017年08月16/8/11日 14:31
 */
class Dialog(context: Context) : android.app.Dialog(context, R.style.DialogStyle) {
    private val mMessageText: TextView
    private val dialogTitleView: TextView
    private val mNegativeBtn: Button
    private val mPositiveBtn: Button
    private val mMiddleDivideLine: View
    private val mDividerLine: View
    private val mButtonLayout: LinearLayout
    private val mContext = context

    private var onNegativeButtonClicked: ((negativeButton: Button) -> Unit)? = null
    private var onPositiveButtonClicked: ((positiveButton: Button) -> Unit)? = null

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        setContentView(R.layout.common_dialog)

        dialogTitleView = findViewById(R.id.dialog_title)
        mMessageText = findViewById(R.id.text_message)
        mNegativeBtn = findViewById(R.id.btn_negative)
        mPositiveBtn = findViewById(R.id.btn_positive)
        mDividerLine = findViewById(R.id.divider_line)
        mMiddleDivideLine = findViewById(R.id.middle_divider_line)
        mButtonLayout = findViewById(R.id.layout_button)

        mNegativeBtn.onSingleClick {
            onNegativeButtonClicked?.invoke(mNegativeBtn)
            dismiss()
        }

        mPositiveBtn.onSingleClick {
            onPositiveButtonClicked?.invoke(mPositiveBtn)
            dismiss()
        }
    }

    fun showTitle(titleText: String = "提示", showTitle: Boolean = true) {
        dialogTitleView.text = titleText
        dialogTitleView.visibility = if (showTitle) View.VISIBLE else View.GONE
    }

    fun message(message: CharSequence) {
        mMessageText.text = message
    }

    fun message(@StringRes stringRes: Int) {
        message(mContext.getString(stringRes))
    }

    fun negativeButton(visible: Boolean = true,
                       text: CharSequence = "取消",
                       onNegativeButtonClicked: ((negativeButton: Button) -> Unit)? = null) {
        this.onNegativeButtonClicked = onNegativeButtonClicked
        mNegativeBtn.text = text
        mNegativeBtn.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun positiveButton(visible: Boolean = true,
                       text: CharSequence = "确定",
                       onPositiveButtonClicked: ((positiveButton: Button) -> Unit)? = null) {
        this.onPositiveButtonClicked = onPositiveButtonClicked
        mPositiveBtn.text = text
        mPositiveBtn.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updateButtonLayoutVisibility() {
        mButtonLayout.visibility = if (mNegativeBtn.visibility == View.GONE &&
                mPositiveBtn.visibility == View.GONE) View.GONE else View.VISIBLE
        mDividerLine.visibility = if (mNegativeBtn.visibility == View.GONE &&
                mPositiveBtn.visibility == View.GONE) View.GONE else View.VISIBLE
        mMiddleDivideLine.visibility = if (mNegativeBtn.visibility == View.VISIBLE
                && mPositiveBtn.visibility == View.VISIBLE) View.VISIBLE else View.GONE
    }

    override fun show() {
        updateButtonLayoutVisibility()
        super.show()
    }
}

fun Context.dialog(message: CharSequence,
                   init: (Dialog.() -> Unit)? = null): Dialog {
    val dialog = Dialog(this)
    dialog.message(message)
    if (null != init) {
        dialog.init()
    }
    return dialog
}

fun Fragment.dialog(message: CharSequence,
                    init: (Dialog.() -> Unit)? = null): Dialog {
    return activity!!.dialog(message, init)
}