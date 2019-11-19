package com.zilchzz.baselibrary.base

import android.support.v4.app.FragmentManager
import com.zilchzz.baselibrary.ext.isActive
import org.jetbrains.anko.bundleOf
import java.util.*
import kotlin.reflect.KClass

/**
 * Smooth fragment manger
 *
 * @author ouyangfeng 2018-04-20 13:53
 */
class SmoothFragmentManager(fragmentManager: FragmentManager) {
    private var mFragmentManager = fragmentManager
    private var mFragmentStack: ArrayList<BaseFragment> = ArrayList()

    companion object {
        private val mFragmentManagerStack = HashMap<BaseActivity, SmoothFragmentManager>()

        fun get(activity: BaseActivity): SmoothFragmentManager {
            var instance = mFragmentManagerStack[activity]

            if (null == instance) {
                instance = SmoothFragmentManager(activity.supportFragmentManager)
                mFragmentManagerStack.put(activity, instance)
            }

            return instance
        }

        fun recycle(activity: BaseActivity) {
            mFragmentManagerStack.remove(activity)?.mFragmentStack?.clear()
        }
    }

    fun switchTo(containerId: Int,
                 fragment: KClass<out BaseFragment>,
                 vararg params: Pair<String, Any>): Boolean {
        val transaction = mFragmentManager.beginTransaction()
        var targetFragment = mFragmentManager.findFragmentByTag(fragment.java.name)
                as? BaseFragment
        if (null == targetFragment || !targetFragment.isActive()) {
            targetFragment = fragment.java.newInstance()
            targetFragment!!.arguments = bundleOf(*params)
            transaction.add(containerId, targetFragment, fragment.java.name)
            addToStack(targetFragment)
        } else {
            mFragmentStack.filter {
                it.javaClass.name != fragment.java.name
            }.forEach {
                transaction.hide(it)
            }
            targetFragment.arguments!!.clear()
            targetFragment.arguments!!.putAll(bundleOf(*params))
            transaction.show(targetFragment)
        }
        transaction.commitAllowingStateLoss()
        return true
    }

    private fun addToStack(fragment: BaseFragment) {
        if (!mFragmentStack.contains(fragment)) {
            mFragmentStack.add(fragment)
        }
    }
}