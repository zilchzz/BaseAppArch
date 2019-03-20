package com.zilchzz.baselibrary.exception

/**
 * 在Activity中切换Fragment，如果没有指定容器ID，将抛出该异常
 *
 * @author zhongzhanzhong 2018-04-06 11:41
 */
class NoContainerIdException(message: String) : RuntimeException(message)