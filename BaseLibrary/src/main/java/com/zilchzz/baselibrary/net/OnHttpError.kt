package com.zilchzz.baselibrary.net

/**
 * Http错误统一接口
 *
 * @author ouyangfeng 2018-04-14 11:57
 */
interface OnHttpError {
    fun onError(httpCode: HttpCode, code: Int?, error: String?)
}