package com.zilchzz.baselibrary.exception

/**
 * Server exception
 *
 * @author ouyangfeng 2018-04-14 10:06
 */
data class ServerException(var code: Int, var error: String?) : Exception(error)