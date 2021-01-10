package com.prejade.ktadmin.modules.sys.dao

import com.prejade.ktadmin.common.BaseRepository
import com.prejade.ktadmin.common.BaseRepositoryImpl
import com.prejade.ktadmin.common.Status
import com.prejade.ktadmin.modules.sys.entity.SysUser
import com.prejade.ktadmin.modules.sys.model.DataPermissionModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.lang.StringBuilder
import javax.persistence.EntityManager

@Repository
interface SysUserRepository : BaseRepository<SysUser, Int> {
    fun getByUsername(username: String): SysUser?

    fun findList(
        dataPermissionModel: DataPermissionModel,
        depId: Int?,
        name: String?,
        status: String?,
        of: Pageable
    ): Page<SysUser>

    fun findByMobileAndStatus(mobile: String, status: Status): SysUser?

    fun findByEmailAndStatus(email: String, normal: Status): SysUser?
}

class SysUserRepositoryImpl(private val entityManager: EntityManager) : BaseRepositoryImpl<SysUser>() {
    fun findList(
        dataPermissionModel: DataPermissionModel,
        depId: Int?,
        name: String?,
        status: String?,
        page: Pageable
    ): Page<SysUser> {
        val hql = StringBuilder("from SysUser entity where entity.status!='${Status.DELETE.name}'")

        if (depId != null) hql.append("and entity.dep.fullPath like '%,${depId},%'")
        if (name != null) hql.append(" and entity.name like '%$name%'")
        if (!dataPermissionModel.all) {
            hql.append(" and (")
            if (dataPermissionModel.deps.isNotEmpty()) {
                hql.append("entity.dep.id in(${dataPermissionModel.deps.joinToString(",")})")
                hql.append(" or ")
            }
            if (dataPermissionModel.users.isNotEmpty()) {
                hql.append("entity.id in(${dataPermissionModel.users.joinToString(",")})")
            }
            hql.append(")")
        }

        return findPageDataByHql("select entity $hql", "select count(*) $hql", page)
    }

    override fun getEntityManager(): EntityManager {
        return entityManager
    }
}
