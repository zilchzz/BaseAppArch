package com.zilchzz.baselibrary.exception

import com.zilchzz.baselibrary.net.HttpCode

/**
 * 接口异常封装类
 *
 * @author ouyangfeng 2018-04-14 12:56
 */
data class ExceptionWrapper(var httpCode: HttpCode, var ex: ServerException): Exception()