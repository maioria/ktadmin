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
    /**
     * 在线用户的key
     */
    fun getOnlineUserKey(token: String): String {
        return "${getOnlineUserKeyPrefix()}$token"
    }

    fun getOnlineUserKeyPrefix(): String {
        return "online_user_key_"
    }

    /**
     * 登录用户的用户名下的token，每一个新的用户登录都会进行更新
     */
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
