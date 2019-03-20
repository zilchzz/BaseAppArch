package com.zilchzz.baselibrary.net

import com.zilchzz.baselibrary.net.HttpCode

/**
 * Http错误统一接口
 *
 * @author zhongzhanzhong 2018-04-14 11:57
 */
interface OnHttpError {
    fun onError(httpCode: HttpCode, code: Int?, error: String?)
}