package com.prejade.ktadmin.rest

import com.google.code.kaptcha.Constants
import com.prejade.ktadmin.JwtTokenUtils
import com.prejade.ktadmin.annotation.Log
import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.common.ServletUtils
import com.prejade.ktadmin.modules.monitor.service.OnlineUserService
import com.prejade.ktadmin.modules.sys.service.MainService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.constraints.NotBlank


@RestController
@RequestMapping("auth")
class LoginRest(
    private val mainService: MainService,
    private val onlineUserService: OnlineUserService
) {

    @PostMapping("login")
    fun login(@RequestBody loginDto: LoginDto, request: HttpServletRequest): HttpResult {
        val errorCountStr = request.session.getAttribute("errorCount")
        if (errorCountStr != null && errorCountStr.toString().toInt() >= 3) {
            val sessionCaptcha = request.session.getAttribute(Constants.KAPTCHA_SESSION_KEY)
            if (sessionCaptcha == null || loginDto.captcha === null) return HttpResult.ok("请输入验证码")
            if (loginDto.captcha !== sessionCaptcha) return HttpResult.ok("验证码不正确")
        }

        if (mainService.disabled(loginDto.username)) return HttpResult.ok("用户已被冻结")

        val user = mainService.login(loginDto.username, loginDto.password, ServletUtils.getIp(request))
        return if (user == null) {
            var errorCount = errorCountStr?.toString()?.toInt() ?: 1
            errorCount++
            request.session.setAttribute("errorCount", errorCount)
            HttpResult.ok("帐号/密码不正确")
        } else {
            val token = mainService.initToken(request, loginDto.username, loginDto.password)
            onlineUserService.addOnlineUser(user, token, request)
            HttpResult.ok(TokenResult(token))
        }
    }

    @PostMapping("2step-code")
    fun twoStepCode(): HttpResult {
        return HttpResult.ok(false)
    }

    @Log("退出登录")
    @PostMapping("logout")
    fun logout(request: HttpServletRequest): HttpResult {
        onlineUserService.removeOnlineUser(JwtTokenUtils.getToken(request)!!)
        return HttpResult.ok(true)
    }

    class LoginDto {
        @NotBlank
        lateinit var username: String

        @NotBlank
        lateinit var password: String
        var captcha: String? = null
    }

    data class TokenResult(var token: String)
}
