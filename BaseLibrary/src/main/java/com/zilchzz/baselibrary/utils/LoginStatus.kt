package com.zilchzz.baselibrary.utils

import com.zilchzz.baselibrary.base.Event
import com.zilchzz.baselibrary.bean.LoginBean
import org.greenrobot.eventbus.EventBus

object LoginStatus {

    var sid: String? by Preference("sid", "")
    var phone: String? by Preference("phone", "")
    var memberId: String? by Preference("member_id", "")
    var hasLogin: Boolean by Preference("hasLogin", false)

    fun loginSuccess(bean: LoginBean?) {
        if (bean == null) {
            return
        }
        hasLogin = true
        sid = bean.sid
        phone = bean.phone
        memberId = bean.member_id
        EventBus.getDefault().post(Event(Event.TYPE_LOGIN_SUCCESS, bean))
    }

    fun loginOut() {
        hasLogin = false
        sid = ""
        phone = ""
        memberId = ""
        EventBus.getDefault().post(Event(Event.TYPE_LOGOUT_SUCCESS))
    }
}