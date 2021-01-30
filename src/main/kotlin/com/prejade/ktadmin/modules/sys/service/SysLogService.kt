package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.common.BaseService
import com.prejade.ktadmin.common.PageBuilder
import com.prejade.ktadmin.common.PageModel
import com.prejade.ktadmin.modules.sys.convert.SysLogConvert
import com.prejade.ktadmin.modules.sys.dao.LogRepository
import com.prejade.ktadmin.modules.sys.entity.SysLog
import com.prejade.ktadmin.modules.sys.model.SysLogModel
import com.prejade.ktadmin.modules.sys.model.MethodLogModel
import org.springframework.beans.BeanUtils
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SysLogService(
    private val repository: LogRepository,
    private val convert: SysLogConvert
) : BaseService<SysLog, Int>() {
    override fun getRepository(): JpaRepository<SysLog, Int> {
        return repository
    }

    fun findList(keyword: String?, pageNo: Int?, pageSize: Int?): PageModel<SysLogModel> {
        return convert.castModel(repository.findList(keyword, PageBuilder.of(pageNo, pageSize)))
    }

    fun methodLogSave(model: MethodLogModel) {
        val entity = SysLog()
        BeanUtils.copyProperties(model, entity)
        save(entity)
    }
}
