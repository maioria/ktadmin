package com.prejade.ktadmin.aspect

import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Component
class ResponseLogAspect {
    @Pointcut("@annotation(com.prejade.ktadmin.annotation.ResponseCode)")
    fun logPointcut() {
    }


}
