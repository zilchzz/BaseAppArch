package com.zilchzz.baselibrary.net

import com.zilchzz.baselibrary.exception.DataEmptyException
import com.zilchzz.baselibrary.exception.ExceptionWrapper
import com.zilchzz.baselibrary.exception.ServerException
import com.zilchzz.baselibrary.net.bean.BaseResp
import io.reactivex.functions.Function

/**
 * 拦截业务异常，并完成Data数据解析
 *
 * @author zhongzhanzhong 2018-04-14 12:51
 */
class DataParseInterceptor<T> : Function<BaseResp<T>, T> {

    override fun apply(result: BaseResp<T>): T {
        if (BaseResp.CODE_SUCCESS != result.code) {
            throw ExceptionWrapper(
                    HttpCode.STATUS_OK, ServerException(result.code, result.message)
            )
        }

        if (null == result.data) {
            throw DataEmptyException()
        }

        return result.data
    }
}