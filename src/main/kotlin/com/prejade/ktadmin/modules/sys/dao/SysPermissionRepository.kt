package com.prejade.ktadmin.modules.sys.dao

import com.prejade.ktadmin.common.BaseRepository
import com.prejade.ktadmin.modules.sys.entity.SysPermission
import org.springframework.stereotype.Repository

@Repository
interface SysPermissionRepository : BaseRepository<SysPermission, Int> {
    fun findByNameIn(names: List<String>): List<SysPermission>
}
