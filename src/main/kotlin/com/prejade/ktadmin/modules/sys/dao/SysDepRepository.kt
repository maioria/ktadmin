package com.prejade.ktadmin.modules.sys.dao

import com.prejade.ktadmin.common.BaseRepository
import com.prejade.ktadmin.common.BaseRepositoryImpl
import com.prejade.ktadmin.common.Status
import com.prejade.ktadmin.modules.sys.entity.SysDep
import com.prejade.ktadmin.modules.sys.model.DataPermissionModel
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.lang.StringBuilder
import javax.persistence.EntityManager


@Repository
interface SysDepRepository : BaseRepository<SysDep, Int> {
    fun findList(dataPermissionModel: DataPermissionModel, search: String?): List<SysDep>

    @Query("select d from SysDep d where d.status='NORMAL'")
    override fun findAll(): List<SysDep>

    fun findByIdIn(split: List<Int>): List<SysDep>

    @Query("select entity.id from SysDep entity where entity.status='NORMAL' and entity.fullPath like ?1")
    fun findIdByParentId(id: String): List<Int>
}

class SysDepRepositoryImpl(private val entityManager: EntityManager) : BaseRepositoryImpl<SysDep>() {
    fun findList(dataPermissionModel: DataPermissionModel, search: String?): List<SysDep> {
        val hql = StringBuilder("from SysDep entity where entity.status!='${Status.DELETE.name}'")
        if (search != null) hql.append(" and entity.name like '%$search%'")
        if (!dataPermissionModel.all) {
            if (dataPermissionModel.deps.isEmpty()) return mutableListOf()
            else hql.append(" and entity.id in (${dataPermissionModel.deps.joinToString(",")})")
        }
        return findByHql(hql.toString())
    }

    override fun getEntityManager(): EntityManager {
        return entityManager
    }
}
