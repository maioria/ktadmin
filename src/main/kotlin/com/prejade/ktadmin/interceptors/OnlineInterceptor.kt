package com.prejade.ktadmin.interceptors

import com.prejade.ktadmin.JwtTokenUtils
import com.prejade.ktadmin.common.ServletUtils
import com.prejade.ktadmin.modules.main.service.MainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OnlineInterceptor : HandlerInterceptor {
    @Autowired
    @Lazy
    private lateinit var mainService: MainService
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = JwtTokenUtils.getToken(request)!!

        return if (mainService.checkOnline(token)) {
            super.preHandle(request, response, handler)
        } else {
            ServletUtils.outputJson(response, 403, "用户未登录")
            false
        }
    }
}
