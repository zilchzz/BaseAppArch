package com.zilchzz.baselibrary.net

import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Error interceptor
 *
 * @author ouyangfeng 2018-04-14 12:47
 */
class ErrorInterceptor<T> : Function<Throwable, Observable<T>> {
    override fun apply(t: Throwable): Observable<T> {
        return Observable.error(t)
    }
}