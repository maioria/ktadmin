package com.prejade.ktadmin

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc


@Configuration
@EnableSwagger2WebMvc
class SwaggerConfig {
    @Bean
    fun createRestApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.practice.tokendemo"))
            .paths(PathSelectors.any())
            .build() //Lists工具类的newArrayList方法将对象转为ArrayList
//            .securitySchemes(apiKey()) //结果是Swagger-ui上出现Authorize，可以手动点击输入token
    }

    /**
     * 构建Authorization验证key
     * @return
     */
    private fun apiKey(): ApiKey {
        return ApiKey(
            "Constant.TOKEN_HEADER_STRING",
            "Constant.TOKEN_HEADER_STRING",
            "header"
        ) //配置输入token的备注 TOKEN_HEADER_STRING = "Authorization"
    }

    /**
     * 构建API文档的详细信息方法
     * @return
     */
    fun apiInfo(): ApiInfo {
        return ApiInfoBuilder() //API页面标题
            .title("ktadmin") //创建者
            .contact(Contact("maioria", "https://mp.csdn.net/console/article", "")) //版本
            .version("1.0") //描述
            .description("API描述")
            .build()
    }
}
