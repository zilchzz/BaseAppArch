package com.zilchzz.baselibrary.net

/**
 * 封装Http请求状态码
 *
 * @author zhongzhanzhong 2018-04-14 12:58
 */
enum class HttpCode(val code: Int) {
    STATUS_OK(200),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    REQUEST_TIMEOUT(408),
    INTERNAL_SERVER_ERROR(500),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504),
    UNKNOWN(-1),
    DNS_ERROR(-2),
    NETWORK_NOT_CONNECTED(-3);


    fun intValue(): Int {
        return code
    }

    companion object {
        fun mapIntValue(code: Int): HttpCode {
            HttpCode.values()
                    .filter { it.code == code }
                    .forEach { return it }

            return UNKNOWN
        }
    }
}