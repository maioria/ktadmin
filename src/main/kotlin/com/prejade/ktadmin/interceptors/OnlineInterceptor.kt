package com.prejade.ktadmin.interceptors

import com.prejade.ktadmin.JwtTokenUtils
import com.prejade.ktadmin.common.ServletUtils
import com.prejade.ktadmin.modules.monitor.service.OnlineUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 判断用户是否在线
 * @author zhangt
 */
@Component
class OnlineInterceptor : HandlerInterceptor {
    @Autowired
    @Lazy
    private lateinit var onlineUserService: OnlineUserService
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = JwtTokenUtils.getToken(request)
        return if (token == null || !onlineUserService.checkOnline(token)) {
            ServletUtils.outputJson(response, 403, "用户未登录")
            false
        } else {
            super.preHandle(request, response, handler)
        }
    }
}
