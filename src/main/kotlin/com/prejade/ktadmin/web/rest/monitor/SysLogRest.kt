package com.prejade.ktadmin.web.rest.monitor

import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.modules.sys.service.SysLogService
import io.swagger.annotations.ApiOperation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ApiOperation("用户操作日志的监控")
@RestController
@RequestMapping("monitor/log")
class SysLogRest(private val service: SysLogService) {
    @PreAuthorize("@ss.hasPermission('monitor:log')")
    @GetMapping("list")
    fun list(keyword: String?, pageNo: Int?, pageSize: Int?): HttpResult {
        return HttpResult.ok(service.findList(keyword, pageNo, pageSize))
    }
}
