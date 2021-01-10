package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.common.BaseService
import com.prejade.ktadmin.modules.sys.dao.SysLogRepository
import com.prejade.ktadmin.modules.sys.entity.SysLog
import com.prejade.ktadmin.modules.sys.model.MethodLogModel
import org.springframework.beans.BeanUtils
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SysLogService(private val repository: SysLogRepository) : BaseService<SysLog, Int>() {
    override fun getRepository(): JpaRepository<SysLog, Int> {
        return repository
    }

    fun methodLogSave(model: MethodLogModel) {
        val entity = SysLog()
        BeanUtils.copyProperties(model, entity)
        save(entity)
    }
}
