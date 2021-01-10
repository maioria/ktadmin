package com.prejade.ktadmin.rest.sys

import com.prejade.ktadmin.annotation.Log
import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.modules.sys.model.AddPermission
import com.prejade.ktadmin.modules.sys.service.SysPermissionService
import io.swagger.annotations.ApiOperation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@ApiOperation("权限")
@RestController
@RequestMapping("sys/permission")
class SysPermissionRest(val service: SysPermissionService) {
    @Log("新增权限")
    @PreAuthorize("@ss.hasPermission('sys:permission:edit')")
    @PostMapping
    fun save(@RequestBody addPermission: AddPermission): HttpResult {
        return HttpResult.ok(service.add(addPermission))
    }

    @Log("编辑权限")
    @PreAuthorize("@ss.hasPermission('sys:permission:edit')")
    @PostMapping("{id}")
    fun upd(@PathVariable id: Int, @RequestBody upd: AddPermission): HttpResult {
        service.upd(id, upd)
        return HttpResult.ok()
    }

    @PreAuthorize("@ss.hasPermission('sys:permission:view')")
    @GetMapping("tree")
    fun tree(): HttpResult {
        return HttpResult.ok(service.tree())
    }

    @PreAuthorize("@ss.hasPermission('sys:permission:edit')")
    @GetMapping("{id}")
    fun get(@PathVariable id: Int): HttpResult {
        return HttpResult.ok(service.getModel(id))
    }

    @PreAuthorize("@ss.hasPermission('sys:permission:edit')")
    @GetMapping("findByName")
    fun findByName(name: String): HttpResult {
        return HttpResult.ok(service.findModelByName(name))
    }

    @Log("删除权限")
    @PreAuthorize("@ss.hasPermission('sys:permission:edit')")
    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Int): HttpResult {
        service.del(id)
        return HttpResult.ok()
    }
}
