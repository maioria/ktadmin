package com.prejade.ktadmin

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import com.prejade.ktadmin.common.BaseCacheClient
import com.prejade.ktadmin.common.CacheClient
import com.prejade.ktadmin.interceptors.OnlineInterceptor
import com.prejade.ktadmin.interceptors.TokenInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*

@Configuration
class WebConfig : WebMvcConfigurer {
    @Autowired
    lateinit var tokenInterceptor: TokenInterceptor

    @Autowired
    lateinit var onlineInterceptor: OnlineInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**")
            .excludePathPatterns(*SysConstant.getNotLoginUrls())
        registry.addInterceptor(onlineInterceptor).addPathPatterns("/**")
            .excludePathPatterns(*SysConstant.getNotLoginUrls())
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowCredentials(true)
            .allowedMethods("GET", "POST", "DELETE", "PUT")
            .maxAge(3600)
    }

    @Bean
    fun producer(): DefaultKaptcha {
        val properties = Properties()
        properties["kaptcha.border"] = "no"
        properties["kaptcha.textproducer.font.color"] = "black"
        properties["kaptcha.textproducer.char.space"] = "5"
        val config = Config(properties)
        val defaultKaptcha = DefaultKaptcha()
        defaultKaptcha.config = config
        return defaultKaptcha
    }

    @Bean
    fun cacheClient(): CacheClient {
        return BaseCacheClient()
    }
}
