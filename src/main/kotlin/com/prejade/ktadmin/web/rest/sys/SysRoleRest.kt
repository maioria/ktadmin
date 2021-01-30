package com.prejade.ktadmin.web.rest.sys

import com.prejade.ktadmin.annotation.Log
import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.modules.sys.model.AddRole
import com.prejade.ktadmin.modules.sys.service.SysRoleService
import io.swagger.annotations.ApiOperation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@ApiOperation("角色")
@RestController
@RequestMapping("sys/role")
class SysRoleRest(val service: SysRoleService) {
    @PreAuthorize("@ss.hasPermission('sys:role:view')")
    @GetMapping("all")
    fun list(): HttpResult {
        return HttpResult.ok(service.all())
    }

    @PreAuthorize("@ss.hasPermission('sys:role:view')")
    @GetMapping("dataPermissionTypes")
    fun dataPermissionTypes(): HttpResult {
        return HttpResult.ok(service.dataPermissionTypes())
    }

    @PreAuthorize("@ss.hasPermission('sys:role:view')")
    @GetMapping("list")
    fun list(orgId: Int?, pageNo: Int?, pageSize: Int?): HttpResult {
        return HttpResult.ok(service.findList(orgId, pageNo, pageSize))
    }

    @PreAuthorize("@ss.hasPermission('sys:role:edit')")
    @GetMapping("{id}")
    fun get(@PathVariable id: Int): HttpResult {
        return HttpResult.ok(service.getModel(id))
    }

    @Log("新增角色")
    @PreAuthorize("@ss.hasPermission('sys:role:edit')")
    @PostMapping
    fun add(model: AddRole): HttpResult {
        return HttpResult.ok(service.add(model))
    }

    @Log("编辑角色")
    @PreAuthorize("@ss.hasPermission('sys:role:edit')")
    @PostMapping("{id}")
    fun edit(@PathVariable id: Int, model: AddRole): HttpResult {
        service.upd(id, model)
        return HttpResult.ok()
    }

    @Log("删除角色")
    @PreAuthorize("@ss.hasPermission('sys:role:edit')")
    @DeleteMapping("{id}")
    fun del(@PathVariable id: Int): HttpResult {
        service.del(id)
        return HttpResult.ok()
    }
}
