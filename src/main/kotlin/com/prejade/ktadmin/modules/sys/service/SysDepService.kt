package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.common.BaseService
import com.prejade.ktadmin.common.StringUtils
import com.prejade.ktadmin.modules.sys.dao.SysDepRepository
import com.prejade.ktadmin.modules.sys.entity.SysDep
import com.prejade.ktadmin.modules.sys.convert.SysDepConvert
import com.prejade.ktadmin.modules.sys.model.AddDep
import com.prejade.ktadmin.modules.sys.model.DataPermissionModel
import com.prejade.ktadmin.modules.sys.model.DepTree
import com.prejade.ktadmin.modules.sys.model.SysDepModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SysDepService(
    private val repository: SysDepRepository,
    private val convert: SysDepConvert
) : BaseService<SysDep, Int>() {

    override fun getRepository(): JpaRepository<SysDep, Int> {
        return repository
    }

    fun tree(dataPermissionModel: DataPermissionModel, search: String?): List<DepTree> {
        return convert.castTree(repository.findList(dataPermissionModel, search))
    }

    fun findSubIds(id: Int): List<Int> {
        return repository.findIdByParentId("%,$id,%")
    }

    fun add(model: AddDep): SysDep {
        val entity = convert.castEntity(model)
        save(entity)
        initFullPath(entity)
        return entity
    }

    fun getModel(id: Int): SysDepModel {
        return convert.castModel(get(id))
    }

    fun upd(id: Int, model: AddDep) {
        val entity = get(id)
        convert.copyProperties(entity, convert.castEntity(model))
        save(entity)
        initFullPath(entity)
    }


    private fun initFullPath(dep: SysDep) {
        if (dep.id == null) throw NullPointerException()
        dep.fullPath = "${dep.id.toString()},"
        var parent = dep.parent
        while (parent != null) {
            dep.fullPath = "${parent.id.toString()},${dep.fullPath}"
            parent = parent.parent
        }
        dep.fullPath = ",${dep.fullPath}"
        save(dep)
    }

    fun findByIds(ids: String): List<SysDep> {
        if (StringUtils.isEmpty(ids)) return arrayListOf()
        return repository.findByIdIn(StringUtils.castIntList(ids))
    }
}
