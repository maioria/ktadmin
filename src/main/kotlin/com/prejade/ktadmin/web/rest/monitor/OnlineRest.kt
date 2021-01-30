package com.prejade.ktadmin.web.rest.monitor

import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.modules.monitor.service.OnlineUserService
import io.swagger.annotations.ApiOperation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@ApiOperation("登录用户的监控")
@RestController
@RequestMapping("monitor/online")
class OnlineRest(private val service: OnlineUserService) {
    @GetMapping("list")
    @PreAuthorize("@ss.hasPermission('monitor:online')")
    fun get(
        @RequestParam(defaultValue = "1") pageNo: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        keyword: String?
    ): HttpResult {
        return HttpResult.ok(service.pageData(pageNo, pageSize, keyword))
    }

    @ApiOperation("踢出用户")
    @PostMapping("kick")
    @PreAuthorize("@ss.hasPermission('monitor:online:kick')")
    fun kickOut(keys: String): HttpResult {
        service.kickOut(keys)
        return HttpResult.ok()
    }
}
