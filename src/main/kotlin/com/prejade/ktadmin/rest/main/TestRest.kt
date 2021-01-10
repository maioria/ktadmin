package com.prejade.ktadmin.rest.main

import com.prejade.ktadmin.common.HttpResult
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class TestRest {
    @ApiOperation("同时只能在线一个用户的测试地址")
    @GetMapping("notMultiLogin")
    fun twoStepCode(): HttpResult {
        return HttpResult.ok(true)
    }
}
