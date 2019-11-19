package com.zilchzz.baselibrary.widgets

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.widget.ImageView
import com.zilchzz.baselibrary.R
import org.jetbrains.anko.find

/**
 * Created by ouyangfeng on 2018/3/16.
 */
class ProgressLoading private constructor(context: Context, theme: Int) : Dialog(context, theme) {
    companion object {
        private lateinit var mDialog: ProgressLoading
        private lateinit var animDrawable: AnimationDrawable

        fun create(context: Context): ProgressLoading {
            mDialog = ProgressLoading(context, R.style.LightProgressDialog)
            mDialog.setContentView(R.layout.common_progress_dialog)
            mDialog.setCancelable(false) //back键能不能取消
            mDialog.setCanceledOnTouchOutside(false) //点击外部能不能取消
            mDialog.window.attributes.gravity = Gravity.CENTER

            val lp = mDialog.window.attributes
            lp.dimAmount = .5f

            mDialog.window.attributes = lp

            val loadingView = mDialog.find<ImageView>(R.id.iv_loading)
            animDrawable = loadingView.background as AnimationDrawable

            return mDialog
        }
    }

    fun hideLoading() {
        super.dismiss()
        animDrawable.stop()
    }

    fun showLoading() {
        super.show()
        animDrawable.start()
    }
}