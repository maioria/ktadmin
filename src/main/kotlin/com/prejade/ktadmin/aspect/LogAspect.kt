package com.prejade.ktadmin.aspect

import com.fasterxml.jackson.databind.ObjectMapper
import com.prejade.ktadmin.annotation.Log
import com.prejade.ktadmin.SecurityUtils
import com.prejade.ktadmin.common.Ip2Region
import com.prejade.ktadmin.common.ServletUtils
import com.prejade.ktadmin.common.StringUtils
import com.prejade.ktadmin.modules.sys.model.MethodLogModel
import com.prejade.ktadmin.modules.sys.service.SysLogService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.lang.reflect.Method
import java.util.ArrayList

@Component
@Aspect
class LogAspect2(private val service: SysLogService, private val objectMapper: ObjectMapper) {

    var currentTime = ThreadLocal<Long>()

    @Pointcut("@annotation(com.prejade.ktadmin.annotation.Log)")
    fun logPointcut() {
    }

    @Around("logPointcut()")
    fun logAround(joinPoint: ProceedingJoinPoint): Any {
        val model = MethodLogModel()
        currentTime.set(System.currentTimeMillis())
        val result = joinPoint.proceed()
        model.username = SecurityUtils.getLoginUser()?.username
        model.time = System.currentTimeMillis() - currentTime.get()
        currentTime.remove()
        val request = ServletUtils.getHttpServletRequest()
        model.ip = ServletUtils.getIp(request)
        if (model.ip != null)
            model.address = Ip2Region.parseIp(model.ip)
        model.browser = ServletUtils.getBrowser(request)
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        model.agent = request.getHeader("User-Agent")
        model.method = joinPoint.target.javaClass.name + "." + signature.name + "()"
        model.params = getParameter(method, joinPoint.args)
        model.description = method.getAnnotation(Log::class.java).value
        service.methodLogSave(model)
        return result
    }

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
