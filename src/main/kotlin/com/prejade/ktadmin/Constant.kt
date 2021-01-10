package com.prejade.ktadmin

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "site")
class ConfigConstant {
    lateinit var uploadPath: String
    var singleLogin: Boolean = false
}


object SysConstant {
    fun getOnlineUserKey(token: String): String {
        return "online_user_key_$token"
    }

    fun getLoginTokenKey(username: String): String {
        return "login_user_$username"
    }

    fun getNotLoginUrls(): Array<String> {
        return listOf(
            "/auth/login", "/auth/logout", "/auth/2step-code",
            "/captcha.jpg**", "/file/*", "/actuator/**",
            "/swagger-ui.html", "/swagger-resources/**",
            "/v2/api-docs", "/webjars/springfox-swagger-ui/**", "/favicon.ico"
        ).toTypedArray()
    }
}
