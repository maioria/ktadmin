package com.prejade.ktadmin.modules.sys.convert

import com.prejade.ktadmin.common.BaseConvert
import com.prejade.ktadmin.modules.sys.entity.SysRole
import com.prejade.ktadmin.modules.sys.enumes.RoleDataPermissionType
import com.prejade.ktadmin.modules.sys.model.AddRole
import com.prejade.ktadmin.modules.sys.model.SysRoleModel
import com.prejade.ktadmin.modules.sys.service.SysDepService
import com.prejade.ktadmin.modules.sys.service.SysPermissionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class SysRoleConvert : BaseConvert<SysRole, SysRoleModel, AddRole>() {

    @Autowired
    @Lazy
    lateinit var permissionService: SysPermissionService

    @Autowired
    @Lazy
    lateinit var depService: SysDepService

    override fun castModel(entity: SysRole): SysRoleModel {
        val model = SysRoleModel()
        model.id = entity.id
        model.name = entity.name
        model.label = entity.label
        model.permissions = entity.permissions.map { it.name }
        model.dataPermissionType = entity.dataPermissionType.name
        model.dataPermissionTypeName = entity.dataPermissionType.getNameValue()
        if (entity.dataPermissionType == RoleDataPermissionType.SETTING) {
            model.depDataPermissions = entity.depDataPermission.map { it.id!! }
        }
        return model
    }

    override fun copyProperties(ori: SysRole, tar: SysRole) {
        ori.name = tar.name
        ori.label = tar.label
        ori.permissions = tar.permissions
        ori.dataPermissionType = tar.dataPermissionType
        ori.depDataPermission = tar.depDataPermission
    }

    override fun castEntity(model: AddRole): SysRole {
        val entity = SysRole()
        entity.name = model.name
        entity.label = model.label
        if (model.permissions != null) entity.permissions = permissionService.findByNames(model.permissions!!)
        entity.dataPermissionType = RoleDataPermissionType.valueOf(model.dataPermissionType)
        if (entity.dataPermissionType == RoleDataPermissionType.SETTING) {
            entity.depDataPermission = depService.findByIds(model.depDataPermissions!!)
        }
        return entity
    }
}
