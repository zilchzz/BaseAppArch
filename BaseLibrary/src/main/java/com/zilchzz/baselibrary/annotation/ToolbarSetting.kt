package com.zilchzz.baselibrary.annotation

/**
 * 该注解旨在简化Toolbar基础设置工作，通过注解自动完成Toolbar基础设置
 *
 * @author ouyangfeng 2017-04-07 10:41
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ToolbarSetting(val displayNavigationIcon: Boolean = true,
                                val navigationDrawable: Int = -1,
                                val title: String = "",
                                val titleRes: Int = -1,
                                val rightText: String = "",
                                val rightTextRes: Int = -1,
                                val titleColor: String = "#000000",
                                val rightTextColor: String = "#000000",
                                val backgroundColor: String = "#FCFCFC")