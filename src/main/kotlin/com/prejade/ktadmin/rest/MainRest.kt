package com.prejade.ktadmin.rest

import com.google.code.kaptcha.Constants
import com.google.code.kaptcha.Producer
import com.prejade.ktadmin.SecurityUtils
import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.modules.sys.service.MainService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


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
        val userId = SecurityUtils.getLoginUser().id
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
