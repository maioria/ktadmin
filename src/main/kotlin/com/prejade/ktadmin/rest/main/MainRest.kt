package com.prejade.ktadmin.rest.main

import com.google.code.kaptcha.Constants
import com.google.code.kaptcha.Producer
import com.prejade.ktadmin.JwtTokenUtils
import com.prejade.ktadmin.SecurityUtils
import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.common.ServletUtils
import com.prejade.ktadmin.modules.main.service.MainService
import org.springframework.web.bind.annotation.*
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("auth")
class AuthRest(
    val mainService: MainService
) {
    @PostMapping("2step-code")
    fun twoStepCode(): HttpResult {
        return HttpResult.ok(false)
    }

    @PostMapping("logout")
    fun logout(request: HttpServletRequest): HttpResult {
        val username = SecurityUtils.getLoginUser()?.username
        if (username != null) mainService.removeLoginToken(username)
        val token = JwtTokenUtils.getToken(request)
        if (token != null) mainService.clearOnlineToken(token)
        return HttpResult.ok(true)
    }

    @PostMapping("login")
    fun login(@RequestBody loginDto: LoginDto, request: HttpServletRequest): HttpResult {
        val errorCountStr = request.session.getAttribute("errorCount")
        if (errorCountStr != null && errorCountStr.toString().toInt() >= 3) {
            val sessionCaptcha = request.session.getAttribute(Constants.KAPTCHA_SESSION_KEY)
            if (sessionCaptcha == null || loginDto.captcha === null) return HttpResult.ok("请输入验证码")
            if (loginDto.captcha !== sessionCaptcha) return HttpResult.ok("验证码不正确")
        }
        return when {
            mainService.disabled(loginDto.username) -> {
                HttpResult.ok("用户已被冻结")
            }
            mainService.login(loginDto.username, loginDto.password, ServletUtils.getIp(request)) -> {
                val token = mainService.initToken(request, loginDto.username, loginDto.password)
                mainService.setLoginToken(loginDto.username, token)
                mainService.setOnlineToken(token)
                HttpResult.ok(TokenResult(token))
            }
            else -> {
                var errorCount = errorCountStr?.toString()?.toInt() ?: 1
                errorCount++
                request.session.setAttribute("errorCount", errorCount)
                HttpResult.ok("帐号/密码不正确")
            }
        }
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

/**
 * 主入口
 */
@RestController
@RequestMapping
class MainRest(
    private val mainService: MainService,
    private val producer: Producer
) {
    @GetMapping("user/info")
    fun info(): HttpResult {
        val userId = SecurityUtils.getLoginUser()!!.id
        return HttpResult.ok(mainService.getUserInfo(userId))
    }

    /**
     * 获取验证码
     */
    @GetMapping("captcha.jpg")
    fun captcha(request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader("Cache-Control", "no-store, no-cache")
        response.contentType = "image/jpeg"
        val text = producer.createText()
        val image = producer.createImage(text)
        val out = response.outputStream
        request.session.setAttribute(Constants.KAPTCHA_SESSION_KEY, text)
        ImageIO.write(image, "jpg", out)
        out.close()
    }
}

