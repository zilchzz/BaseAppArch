package com.zilchzz.baselibrary.ext

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 使用RxJava开启一个任务
 * @param delayTime 延迟时间，默认0
 * @param executeThread 执行线程
 * @param task lambda,任务
 */
fun doTask(delayTime: Long = 0, executeThread: Scheduler = AndroidSchedulers.mainThread(), task: () -> Unit): Disposable {
    return Observable.timer(delayTime, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(executeThread)
            .subscribe {
                task()
            }
}