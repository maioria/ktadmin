package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.common.PageBuilder
import com.prejade.ktadmin.common.PageModel
import com.prejade.ktadmin.KtadminException
import com.prejade.ktadmin.common.BaseService
import com.prejade.ktadmin.modules.sys.dao.SysRoleRepository
import com.prejade.ktadmin.modules.sys.entity.SysRole
import com.prejade.ktadmin.modules.sys.convert.SysRoleConvert
import com.prejade.ktadmin.modules.sys.model.AddRole
import com.prejade.ktadmin.modules.sys.model.RoleDataPermissionTypeModel
import com.prejade.ktadmin.modules.sys.model.SysRoleModel
import com.prejade.ktadmin.modules.sys.enumes.RoleDataPermissionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class SysRoleService(
    private val repository: SysRoleRepository,
    private val convert: SysRoleConvert
) : BaseService<SysRole, Int>() {
    override fun getRepository(): JpaRepository<SysRole, Int> {
        return repository
    }

    fun all(): List<SysRoleModel> {
        return convert.castModel(repository.findAll())
    }

    fun findByIds(ids: List<String>): List<SysRole> {
        val params = mutableListOf<Int>()
        ids.forEach { params.add(it.toInt()) }
        return repository.findAllById(params)
    }

    fun findList(orgId: Int?, pageNo: Int?, pageSize: Int?): PageModel<SysRoleModel> {
        return convert.castModel(repository.findList(orgId, PageBuilder.of(pageNo, pageSize)))
    }

    fun getModel(id: Int): SysRoleModel {
        return convert.castModel(get(id))
    }

    fun add(model: AddRole): SysRole {
        val entity = convert.castEntity(model)
        save(entity)
        return entity
    }

    fun upd(id: Int, model: AddRole) {
        val entity = get(id)
        val tarEntity = convert.castEntity(model)
        convert.copyProperties(entity, tarEntity)
        save(entity)
    }

    fun castLabels(names: Set<String>): Set<String> {
        val result = mutableSetOf<String>()
        val data = findByNames(names)
        for (item in data) {
            result.add(item.label)
        }
        return result
    }

    fun findByNames(names: Collection<String>): Set<SysRole> {
        return repository.findByNameIn(names)
    }

    fun dataPermissionTypes(): List<RoleDataPermissionTypeModel> {
        val result = arrayListOf<RoleDataPermissionTypeModel>()
        for (item in RoleDataPermissionType.values()) {
            result.add(RoleDataPermissionTypeModel(item.getNameValue(), item.name))
        }
        return result
    }
}
