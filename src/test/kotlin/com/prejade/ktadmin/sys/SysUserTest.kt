package com.prejade.ktadmin.sys

import com.prejade.ktadmin.modules.sys.model.AddPermission
import com.prejade.ktadmin.modules.sys.model.AddRole
import com.prejade.ktadmin.modules.sys.model.AddUser
import com.prejade.ktadmin.modules.sys.service.SysPermissionService
import com.prejade.ktadmin.modules.sys.service.SysRoleService
import com.prejade.ktadmin.modules.sys.service.SysUserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SysUserTest {
    @Autowired
    lateinit var sysUserService: SysUserService

    @Autowired
    lateinit var roleService: SysRoleService

    @Autowired
    lateinit var permissionService: SysPermissionService

    @Test
    fun add() {
        val model = AddUser()
        model.username = "admin"
        model.nickname = "超级管理员"
        model.depId = 1
        model.roles = "1"
        model.email = "prejade@163.com"
        model.mobile = "13120214196"
        sysUserService.add(model)
    }

    @Test
    fun addRole() {
        val model = AddRole()
        model.name = "admin"
        model.label = "系统管理员"
        model.permissions = "1"
        roleService.add(model)
    }

    @Test
    fun addPrivilege() {
        val model = AddPermission()
        model.label = "角色编辑"
        model.name = "sys:role:edit"
        model.type = "PERMISSION"
        model.parentName = "sys:role"
        permissionService.add(model)
    }

    @Test
    fun addAllPrivilege() {
        val list = mutableListOf(
//            PermissionModel("sys", "系统设置"),
//            PermissionModel("sys:org:view", "公司管理"),
//            PermissionModel("sys:dep:view", "部门管理"),
//            PermissionModel("sys:user:view", "用户管理"),
//            PermissionModel("sys:role:view", "角色管理")
//            PermissionModel("sys:dep:edit", "部门编辑", 3)
//            PermissionModel("sys:org:edit", "公司编辑", 2),
//            PermissionModel("sys:user:edit", "用户编辑", 4)
//            PermissionModel("sys:permission:view", "权限管理", 1)
                    PermissionModel("sys:permission:edit", "权限编辑", "sys")
//            PermissionModel("sys:user:edit", "用户编辑", 4)
        )
        for (item in list) {
            val model = AddPermission()
            model.label = item.label
            model.name = item.name
            model.type = "MENU"
            model.parentName = item.parentName
            permissionService.add(model)
        }
    }

    data class PermissionModel(var name: String, var label: String, val parentName: String? = null)
}
