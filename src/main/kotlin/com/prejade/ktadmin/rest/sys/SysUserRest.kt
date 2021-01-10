package com.prejade.ktadmin.rest.sys

import com.prejade.ktadmin.SecurityUtils
import com.prejade.ktadmin.annotation.Log
import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.modules.sys.model.AddUser
import com.prejade.ktadmin.modules.sys.model.UpdAccount
import com.prejade.ktadmin.modules.sys.service.SysUserService
import io.swagger.annotations.ApiOperation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@ApiOperation("用户")
@RestController
@RequestMapping("sys/user")
class SysUserRest(val service: SysUserService) {
    @Log("新增用户")
    @PreAuthorize("@ss.hasPermission('sys:user:edit')")
    @PostMapping
    fun save(@RequestBody add: AddUser): HttpResult {
        return HttpResult.ok(service.add(add).id)
    }

    @PreAuthorize("@ss.hasPermission('sys:user:edit') and @ss.hasUserPermission(#id)")
    @PostMapping("{id}/updAccount")
    fun updAccount(@PathVariable id: Int, @RequestBody account: UpdAccount): HttpResult {
        service.updAccount(id, account)
        return HttpResult.ok(true)
    }

    @PreAuthorize("@ss.hasPermission('sys:user:edit') and @ss.hasUserPermission(#id)")
    @GetMapping("{id}")
    fun get(@PathVariable id: Int): HttpResult {
        return HttpResult.ok(service.getModel(id))
    }

    @PreAuthorize("@ss.hasPermission('sys:user:edit') and @ss.hasUserPermission(#id)")
    @Log("修改用户")
    @PostMapping("{id}")
    fun upd(@PathVariable id: Int, @RequestBody upd: AddUser): HttpResult {
        service.upd(id, upd)
        return HttpResult.ok()
    }

    @GetMapping("checkMobileExist")
    fun checkMobileExist(mobile: String, id: Int?): HttpResult {
        return HttpResult.ok(service.checkMobileExist(mobile, id))
    }

    @GetMapping("checkEmailExist")
    fun checkEmailExist(email: String, id: Int?): HttpResult {
        return HttpResult.ok(service.checkEmailExist(email, id))
    }

    @PreAuthorize("@ss.hasPermission('sys:user:view')")
    @GetMapping("list")
    fun list(depId: Int?, name: String?, status: String?, pageNo: Int?, pageSize: Int?): HttpResult {
        return HttpResult.ok(
            service.findList(
                SecurityUtils.getLoginUser()!!.dataPermission,
                depId,
                name,
                status,
                pageNo,
                pageSize
            )
        )
    }

    @PostMapping("resetPassword")
    fun resetPwd(userId: Int, password: String): HttpResult {
        service.resetPwd(userId, password)
        return HttpResult.ok(true)
    }

    /**
     * 删除
     */
    @PreAuthorize("@ss.hasPermission('sys:user:edit') and @ss.hasUserPermission(#id)")
    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Int): HttpResult {
        service.del(id)
        return HttpResult.ok()
    }
}
