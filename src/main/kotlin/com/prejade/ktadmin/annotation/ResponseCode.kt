package com.prejade.ktadmin.annotation

/**
 * 接口返回值加上json code 相当于  {code:200,data:{}}
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ResponseCode(
    val statusKey: String = "code",
    val dataKey: String = "data",
    val msgKey: String = "msg"
)
