package com.prejade.ktadmin.modules.sys.convert

import com.prejade.ktadmin.common.BaseConvert
import com.prejade.ktadmin.modules.sys.entity.SysPermission
import com.prejade.ktadmin.modules.sys.enumes.SysPermissionType
import com.prejade.ktadmin.modules.sys.model.AddPermission
import com.prejade.ktadmin.modules.sys.model.PermissionTree
import com.prejade.ktadmin.modules.sys.model.SysPermissionModel
import com.prejade.ktadmin.modules.sys.service.SysPermissionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class SysPermissionConvert : BaseConvert<SysPermission, SysPermissionModel, AddPermission>() {
    @Autowired
    @Lazy
    lateinit var sysPermissionService: SysPermissionService
    override fun castModel(entity: SysPermission): SysPermissionModel {
        val model = SysPermissionModel()
        model.id = entity.id
        model.name = entity.name
        model.label = entity.label
        model.type = entity.type.name
        model.typeName = entity.type.getNameValue()
        model.parentId = entity.parent?.id
        model.parentName = entity.parent?.name
        return model
    }

    override fun copyProperties(ori: SysPermission, tar: SysPermission) {
        ori.name = tar.name
        ori.label = tar.label
        ori.type = tar.type
        ori.parent = tar.parent
    }

    override fun castEntity(model: AddPermission): SysPermission {
        val entity = SysPermission()
        entity.id = model.id
        entity.name = model.name
        entity.label = model.label
        if (model.parentName != null)
            entity.parent = model.parentName?.let { sysPermissionService.findByName(it) }
        entity.type = SysPermissionType.valueOf(model.type)
        return entity
    }

    fun castTree(permissions: List<SysPermission>): List<PermissionTree> {
        val treeModels: MutableList<PermissionTree> = mutableListOf()
        val map = hashMapOf<Int, PermissionTree>()
        var treeModel: PermissionTree
        var parent: SysPermission?
        for (entity in permissions) {
            treeModel = PermissionTree()
            treeModel.id = entity.id
            treeModel.name = entity.name
            treeModel.label = entity.label
            treeModel.type = entity.type.name
            treeModel.typeName = entity.type.getNameValue()
            treeModel.parentName = entity.parent?.name
            parent = entity.parent
            map[entity.id!!] = treeModel
            if (parent != null) {
                map[parent.id]?.addChildren(treeModel)
            } else {
                treeModels.add(treeModel)
            }
        }
        return treeModels
    }

}
