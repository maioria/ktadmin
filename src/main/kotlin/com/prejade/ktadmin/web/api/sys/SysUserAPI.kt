package com.prejade.ktadmin.web.api.sys

import com.prejade.ktadmin.modules.sys.service.SysUserService
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ApiOperation("用户接口")
@RestController
@RequestMapping("api/v1/sys/user")
class SysUserAPI(private val service: SysUserService) {

}
