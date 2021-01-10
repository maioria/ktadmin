package com.prejade.ktadmin.sys

import com.prejade.ktadmin.modules.sys.entity.SysDep
import com.prejade.ktadmin.modules.sys.entity.SysPermission
import com.prejade.ktadmin.modules.sys.entity.SysRole
import com.prejade.ktadmin.modules.sys.entity.SysUser
import com.prejade.ktadmin.modules.sys.enumes.RoleDataPermissionType
import com.prejade.ktadmin.modules.sys.model.AddDep
import com.prejade.ktadmin.modules.sys.model.AddPermission
import com.prejade.ktadmin.modules.sys.model.AddRole
import com.prejade.ktadmin.modules.sys.model.AddUser
import com.prejade.ktadmin.modules.sys.service.SysDepService
import com.prejade.ktadmin.modules.sys.service.SysPermissionService
import com.prejade.ktadmin.modules.sys.service.SysRoleService
import com.prejade.ktadmin.modules.sys.service.SysUserService
import org.springframework.stereotype.Component

@Component
class InitDataComponent(
    private var sysDepService: SysDepService,
    private var sysUserService: SysUserService,
    private var sysPermissionService: SysPermissionService,
    private var sysRoleService: SysRoleService
) {
    fun initData() {
        val permissions = initPermission()
        val role = initRole(permissions)
        val dep = initDep()
        val sysUser = initUser(dep.id!!, role)
        val password = "123456"
        sysUserService.resetPwd(sysUser.id!!, password)
        println("创建用户成功:${sysUser.username},$password")
    }

    fun initDep(): SysDep {
        val model = AddDep()
        model.name = "prejade"
        return sysDepService.add(model)
    }

    fun initPermission(): List<SysPermission> {
        val result = mutableListOf<SysPermission>()
        val list = mutableListOf(
            PermissionModel("sys", "系统设置"),
            PermissionModel("sys:dep:view", "部门管理", parentName = "sys"),
            PermissionModel("sys:user:view", "用户管理", parentName = "sys"),
            PermissionModel("sys:role:view", "角色管理", parentName = "sys"),
            PermissionModel("sys:permission:view", "权限管理", parentName = "sys"),
            PermissionModel("sys:dep:edit", "部门编辑", parentName = "sys:dep:view"),
            PermissionModel("sys:user:edit", "用户编辑", parentName = "sys:user:view"),
            PermissionModel("sys:role:edit", "角色编辑", parentName = "sys:role:view"),
            PermissionModel("sys:permission:edit", "权限编辑", parentName = "sys:permission:view"),
            PermissionModel("monitor", "系统监控"),
            PermissionModel("monitor:online", "在线用户", parentName = "monitor"),
            PermissionModel("monitor:log", "操作日志", parentName = "monitor"),
            PermissionModel("monitor:error", "异常日志", parentName = "monitor"),
            PermissionModel("monitor:server", "服务监控", parentName = "monitor")
        )
        for (item in list) {
            result.add(addPermission(item))
        }
        return result
    }

    fun addPermission(model: PermissionModel): SysPermission {
        val addModel = AddPermission()
        addModel.label = model.label
        addModel.name = model.name
        addModel.type = model.type
        addModel.parentName = model.parentName
        return sysPermissionService.add(addModel)
    }

    fun initRole(permissions: List<SysPermission>): SysRole {
        val model = AddRole()
        model.name = "admin"
        model.label = "系统管理员"
        model.permissions = permissions.joinToString(",") { it.name }
        model.dataPermissionType = RoleDataPermissionType.ALL.name
        return sysRoleService.add(model)
    }

    fun initUser(depId: Int, role: SysRole): SysUser {
        val model = AddUser()
        model.username = "admin"
        model.name = "超级管理员"
        model.depId = 1
        model.roles = "1"
        model.email = "prejade@163.com"
        model.mobile = "15666666666"
        model.depId = depId
        model.roles = role.name
        return sysUserService.add(model)
    }

    data class PermissionModel(
        var name: String,
        var label: String,
        var type: String = "MENU",
        var parentName: String? = null
    )
}

//INSERT INTO sys_dep (id, create_time, full_path, name, status, parent_id) VALUES (2, '2020-12-19 14:53:30.141000', ',1,2,', '研发部', 'NORMAL', 1);
//INSERT INTO sys_dep (id, create_time, full_path, name, status, parent_id) VALUES (3, '2020-12-19 14:53:56.397000', ',1,3,', '财务部', 'NORMAL', 1);
//INSERT INTO sys_dep (id, create_time, full_path, name, status, parent_id) VALUES (4, '2020-12-20 07:04:31.633000', ',1,4,', '业务部', 'NORMAL', 1);
//INSERT INTO sys_dep (id, create_time, full_path, name, status, parent_id) VALUES (5, '2020-12-20 07:05:00.276000', ',1,4,5,', '业务1部', 'NORMAL', 4);
//INSERT INTO sys_dep (id, create_time, full_path, name, status, parent_id) VALUES (6, '2020-12-20 07:05:35.790000', ',1,4,6,', '业务2部', 'NORMAL', 4);
//INSERT INTO sys_dep (id, create_time, full_path, name, status, parent_id) VALUES (7, '2020-12-20 07:13:12.620000', ',1,4,5,7,', '1部1组', 'NORMAL', 5);
//INSERT INTO sys_dep (id, create_time, full_path, name, status, parent_id) VALUES (8, '2020-12-20 07:13:30.163000', ',1,4,5,8,', '1部2组', 'NORMAL', 5);
//INSERT INTO sys_dep (id, create_time, full_path, name, status, parent_id) VALUES (9, '2021-01-01 07:39:34.878000', ',1,4,6,9,', '2部1组1', 'NORMAL', 6);
//
//INSERT INTO sys_role (id, label, name, data_permission_type) VALUES (2, '测试', 'test', 'DEPARTMENTS');
//INSERT INTO sys_role (id, label, name, data_permission_type) VALUES (7, '业务员', 'sale', 'SETTING');
//
//INSERT INTO sys_user (id, avatar, create_time, email, last_login_ip, last_login_time, mobile, name, password, salt, status, username, dep_id, introduction, tel) VALUES (3, null, '2020-12-20 07:18:54.293000', '15888888888@qq.com', null, null, '15888888888', '业务员1', '38f80b31cf08b2e96d4c2cbfe38b64ee', 'ec7a17827dda4b38b3f1', 'NORMAL', 'yewuyuan1', 7, null, null);
//INSERT INTO sys_user (id, avatar, create_time, email, last_login_ip, last_login_time, mobile, name, password, salt, status, username, dep_id, introduction, tel) VALUES (8, null, '2020-12-27 03:25:40.495000', 'zhangtao_j2ee@163.com', '0:0:0:0:0:0:0:1', '2020-12-27 03:26:51.488', '15777777777', '张涛', '39e7aa4d82f28211a15ea2b13fa14905', '6c540e2c40344748bba4', 'NORMAL', 'zhangtao_j2ee@163.com', 2, '当父母容易，做父母难', '');
