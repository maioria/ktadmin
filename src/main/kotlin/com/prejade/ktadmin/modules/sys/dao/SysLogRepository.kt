package com.prejade.ktadmin.modules.sys.dao

import com.prejade.ktadmin.common.BaseRepository
import com.prejade.ktadmin.common.BaseRepositoryImpl
import com.prejade.ktadmin.modules.sys.entity.SysLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.lang.StringBuilder
import javax.persistence.EntityManager

@Repository
interface LogRepository : BaseRepository<SysLog, Int> {
    fun findList(
        keyword: String?,
        page: Pageable
    ): Page<SysLog>
}

class LogRepositoryImpl(private val entityManager: EntityManager) : BaseRepositoryImpl<SysLog>() {
    fun findList(
        keyword: String?,
        page: Pageable
    ): Page<SysLog> {
        val hql = StringBuilder("from SysLog entity")
        if (keyword != null) {
            hql.append(" and entity.name like '%$keyword%'")
        }
        val selHql = hql.toString().replaceFirst("and", "where")
        return findPageDataByHql("select entity $selHql", "select count(*) $selHql", page)
    }

    override fun getEntityManager(): EntityManager {
        return entityManager
    }
}
