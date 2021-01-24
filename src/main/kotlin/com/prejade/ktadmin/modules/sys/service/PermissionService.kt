package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.SecurityUtils
import org.springframework.stereotype.Service

/**
 * 权限验证的service
 */
@Service("ss")
class PermissionService(private val sysUserService: SysUserService) {
    /**
     * 功能权限
     */
    fun hasPermission(permission: String): Boolean {
        val user = SecurityUtils.getLoginUser()
        if (user.username == "admin") return true
        return user.authorities.map { it.authority }.contains(permission)
    }

    /**
     * 部门的数据权限
     */
    fun hasDepPermission(departmentId: Int): Boolean {
        val user = SecurityUtils.getLoginUser()
        if (user.username == "admin") return true
        val dataPermission = user.dataPermission
        return dataPermission.all || dataPermission.deps.contains(departmentId)
    }

    /**
     * 用户的数据权限
     */
    fun hasUserPermission(userId: Int): Boolean {
        val user = SecurityUtils.getLoginUser()
        if (user.username == "admin") return true
        val dataPermission = user.dataPermission
        if (dataPermission.all) return true
        if (dataPermission.users.contains(userId)) return true
        return dataPermission.deps.contains(sysUserService.getDepId(userId))
    }
}
