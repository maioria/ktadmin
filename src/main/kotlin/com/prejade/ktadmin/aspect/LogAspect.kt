package com.prejade.ktadmin.aspect

import com.fasterxml.jackson.databind.ObjectMapper
import com.prejade.ktadmin.annotation.Log
import com.prejade.ktadmin.SecurityUtils
import com.prejade.ktadmin.common.*
import com.prejade.ktadmin.modules.sys.model.MethodLogModel
import com.prejade.ktadmin.modules.sys.service.SysLogService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.lang.reflect.Method
import java.util.ArrayList

@Component
@Aspect
class LogAspect(private val service: SysLogService, private val objectMapper: ObjectMapper) {
    val logger: Logger = LoggerFactory.getLogger(javaClass)
    var currentTime = ThreadLocal<Long>()

    @Pointcut("@annotation(com.prejade.ktadmin.annotation.Log)")
    fun logPointcut() {
    }

    @Around("logPointcut()")
    fun logAround(joinPoint: ProceedingJoinPoint): Any {
        currentTime.set(System.currentTimeMillis())
        val signature = joinPoint.signature as MethodSignature
        val model = MethodLogModel(
            SecurityUtils.getLoginUser().username,
            signature.method.getAnnotation(Log::class.java).value,
            joinPoint.target.javaClass.name + "." + signature.name + "()",
            getParameter(signature.method, joinPoint.args),
            ServletUtils.getHttpServletRequest()
        )

        logger.debug("start:$model")
        val result = joinPoint.proceed()
        model.time = System.currentTimeMillis() - currentTime.get()
        logger.debug("end:$model")

        currentTime.remove()
        service.methodLogSave(model)
        return result
    }

//    /**
//     * 配置异常通知
//     *
//     * @param joinPoint join point for advice
//     * @param e exception
//     */
//    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
//    fun logAfterThrowing(joinPoint: JoinPoint, e: Throwable) {
//        val signature = joinPoint.signature as MethodSignature
//        val model = MethodLogModel(
//            SecurityUtils.getLoginUser().username,
//            signature.method.getAnnotation(Log::class.java).value,
//            joinPoint.target.javaClass.name + "." + signature.name + "()",
//            getParameter(signature.method, joinPoint.args),
//            ServletUtils.getHttpServletRequest()
//        )
//        model.exceptionDetail = ThrowableUtils.getStackTrace(e).toByteArray()
//        currentTime.remove()
//        logger.error(model.toString(), e)
//        service.methodLogSave(model)
//    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private fun getParameter(method: Method, args: Array<Any>): String? {
        val argList: MutableList<Any> = ArrayList()
        val parameters = method.parameters
        for (i in parameters.indices) {
            //将RequestBody注解修饰的参数作为请求参数
            val requestBody = parameters[i].getAnnotation(RequestBody::class.java)
            if (requestBody != null) {
                argList.add(args[i])
            }
            //将RequestParam注解修饰的参数作为请求参数
            val requestParam = parameters[i].getAnnotation(RequestParam::class.java)
            if (requestParam != null) {
                val map: MutableMap<String, Any> = HashMap()
                var key = parameters[i].name
                if (!StringUtils.isEmpty(requestParam.value)) {
                    key = requestParam.value
                }
                map[key] = args[i]
                argList.add(map)
            }
        }
        if (argList.size == 0) {
            return ""
        }
        return if (argList.size == 1) {
            objectMapper.writeValueAsString(argList[0])
        } else {
            objectMapper.writeValueAsString(argList)
        }
    }

}
