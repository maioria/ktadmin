package com.prejade.ktadmin.interceptors

import com.prejade.ktadmin.ConfigConstant
import com.prejade.ktadmin.JwtTokenUtils
import com.prejade.ktadmin.SecurityUtils
import com.prejade.ktadmin.common.ServletUtils
import com.prejade.ktadmin.modules.main.service.MainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 同一帐户只能登录一次的拦截器
 */
@Component
class TokenInterceptor(
    private val configConstant: ConfigConstant
) : HandlerInterceptor {
    @Autowired
    @Lazy
    private lateinit var mainService: MainService
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (!configConstant.singleLogin) super.preHandle(request, response, handler)

        val token = JwtTokenUtils.getToken(request)!!
        val loginUser = SecurityUtils.getLoginUser()
        return if (mainService.checkLoginToken(loginUser!!.username, token)) {
            super.preHandle(request, response, handler)
        } else {
            ServletUtils.outputJson(response, 403, "token已失效，请重新登录")
            false
        }
    }
}
