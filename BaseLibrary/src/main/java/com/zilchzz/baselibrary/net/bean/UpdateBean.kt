package com.zilchzz.baselibrary.net.bean

class UpdateBean {
    /**
     * newest_version : 1.0.0
     * download_url : https://www.purcow.io
     * force : true
     * description : 更新内容：
     * 1.修复登录态失效的bug
     * 2.升级新功能
     * create_time : 2018-06-27
     */

    var newest_version: String? = null
    var download_url: String? = null
    var force: Boolean = false
    var description: String? = null
    var create_time: String? = null

    override fun toString(): String {
        return "DataBean(newest_version=$newest_version, download_url=$download_url, isForce=$force, description=$description, create_time=$create_time)"
    }
}
