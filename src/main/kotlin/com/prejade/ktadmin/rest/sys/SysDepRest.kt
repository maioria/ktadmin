package com.prejade.ktadmin.rest.sys

import com.prejade.ktadmin.SecurityUtils
import com.prejade.ktadmin.annotation.Log
import com.prejade.ktadmin.common.HttpResult
import com.prejade.ktadmin.modules.sys.model.AddDep
import com.prejade.ktadmin.modules.sys.service.SysDepService
import io.swagger.annotations.ApiOperation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@ApiOperation("部门")
@RestController
@RequestMapping("sys/dep")
class SysDepRest(val service: SysDepService) {
    @Log("新增部门")
    @PreAuthorize("@ss.hasPermission('sys:dep:edit')")
    @PostMapping
    fun save(@RequestBody add: AddDep): HttpResult {
        return HttpResult.ok(service.add(add))
    }

    @Log("编辑部门")
    @PreAuthorize("@ss.hasPermission('sys:dep:edit') and @ss.hasDepPermission(#id)")
    @PostMapping("{id}")
    fun upd(@PathVariable id: Int, @RequestBody upd: AddDep): HttpResult {
        service.upd(id, upd)
        return HttpResult.ok()
    }

    @PreAuthorize("@ss.hasPermission('sys:dep:edit') and @ss.hasDepPermission(#id)")
    @GetMapping("{id}")
    fun get(@PathVariable id: Int): HttpResult {
        return HttpResult.ok(service.getModel(id))
    }

    @GetMapping("tree")
    fun tree(search: String?): HttpResult {
        return HttpResult.ok(service.tree(SecurityUtils.getLoginUser()!!.dataPermission, search))
    }

    @Log("删除部门")
    @PreAuthorize("@ss.hasPermission('sys:dep:edit') and @ss.hasDepPermission(#id)")
    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Int): HttpResult {
        service.del(id)
        return HttpResult.ok()
    }
}
