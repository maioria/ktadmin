package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.common.BaseService
import com.prejade.ktadmin.modules.sys.dao.SysPermissionRepository
import com.prejade.ktadmin.modules.sys.entity.SysPermission
import com.prejade.ktadmin.modules.sys.convert.SysPermissionConvert
import com.prejade.ktadmin.modules.sys.model.AddPermission
import com.prejade.ktadmin.modules.sys.model.PermissionTree
import com.prejade.ktadmin.modules.sys.model.SysPermissionModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class SysPermissionService(
    private val repository: SysPermissionRepository,
    private val convert: SysPermissionConvert
) : BaseService<SysPermission, Int>() {

    override fun getRepository(): JpaRepository<SysPermission, Int> {
        return repository
    }

    fun add(addModel: AddPermission): SysPermission {
        val entity = convert.castEntity(addModel)
        save(entity)
        return entity
    }

    fun getModel(id: Int): SysPermissionModel {
        return convert.castModel(get(id))
    }

    fun tree(): List<PermissionTree> {
        return convert.castTree(repository.findAll())
    }

    fun findAll(): List<SysPermission> {
        return repository.findAll()
    }

    fun findByName(name: String): SysPermission {
        return repository.findByName(name)
    }

    fun findByNames(names: String): Set<SysPermission> {
        return repository.findByNameIn(names.split(",").toSet())
    }

    fun upd(id: Int, model: AddPermission) {
        val entity = get(id)
        convert.copyProperties(entity, convert.castEntity(model))
        save(entity)
    }

    fun findModelByName(name: String): SysPermissionModel {
        return convert.castModel(findByName(name))
    }
}
