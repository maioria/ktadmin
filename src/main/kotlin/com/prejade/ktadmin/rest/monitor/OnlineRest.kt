package com.prejade.ktadmin.rest.monitor

import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.common.PageModel
import com.prejade.ktadmin.modules.sys.model.OnlineUser
import com.prejade.ktadmin.modules.sys.service.OnlineUserService
import io.swagger.annotations.ApiOperation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@ApiOperation("登录用户的监控")
@RestController
@RequestMapping("monitor/online")
class OnlineRest(private val service: OnlineUserService) {
    @GetMapping
    @PreAuthorize("@ss.hasPermission('monitor:online')")
    fun get(
        @RequestParam(defaultValue = "1") pageNo: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): PageModel<OnlineUser> {
        return service.pageData(pageNo, pageSize)
    }

    @ApiOperation("踢出用户")
    @PostMapping("kick")
    fun kickOut(keys: String): HttpResult {
        service.kickOut(keys)
        return HttpResult.ok()
    }
}
