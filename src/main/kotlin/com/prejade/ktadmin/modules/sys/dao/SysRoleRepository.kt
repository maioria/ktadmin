package com.prejade.ktadmin.modules.sys.dao

import com.prejade.ktadmin.common.BaseRepository
import com.prejade.ktadmin.common.BaseRepositoryImpl
import com.prejade.ktadmin.modules.sys.entity.SysRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.lang.StringBuilder
import javax.persistence.EntityManager

@Repository
interface SysRoleRepository : BaseRepository<SysRole, Int> {
    fun findList(orgId: Int?, of: Pageable): Page<SysRole>
    fun findByNameIn(names: List<String>): List<SysRole>
}

class SysRoleRepositoryImpl(private val entityManager: EntityManager) : BaseRepositoryImpl<SysRole>() {
    fun findList(orgId: Int?, page: Pageable): Page<SysRole> {
        val hql = StringBuilder("from SysRole entity")
        if (orgId != null) hql.append(" and entity.sysOrg.id=${orgId}")
        return findPageDataByHql("select entity $hql", "select count(*) $hql", page)
    }

    override fun getEntityManager(): EntityManager {
        return entityManager
    }
}
