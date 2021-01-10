package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.SecurityUtils
import org.springframework.stereotype.Service

@Service("ss")
class PermissionService(private val sysUserService: SysUserService) {
    fun hasPermission(permission: String): Boolean {
        val user = SecurityUtils.getLoginUser() ?: return false
        if (user.username == "admin") return true
        return user.authorities.map { it.authority }.contains(permission)
    }

    fun hasDepPermission(departmentId: Int): Boolean {
        val user = SecurityUtils.getLoginUser() ?: return false
        if (user.username == "admin") return true
        val dataPermission = user.dataPermission
        return dataPermission.all || dataPermission.deps.contains(departmentId)
    }

    fun hasUserPermission(userId: Int): Boolean {
        val user = SecurityUtils.getLoginUser() ?: return false
        if (user.username == "admin") return true
        val dataPermission = user.dataPermission
        if (dataPermission.all) return true
        if (dataPermission.users.contains(userId)) return true
        return dataPermission.deps.contains(sysUserService.getDepId(userId))
    }
}
