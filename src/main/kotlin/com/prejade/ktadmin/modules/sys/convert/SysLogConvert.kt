package com.prejade.ktadmin.modules.sys.convert

import com.prejade.ktadmin.common.BaseConvert
import com.prejade.ktadmin.modules.sys.entity.SysLog
import com.prejade.ktadmin.modules.sys.model.AddSysLogModel
import com.prejade.ktadmin.modules.sys.model.SysLogModel
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Component

@Component
class SysLogConvert : BaseConvert<SysLog, SysLogModel, AddSysLogModel>() {
    override fun castModel(entity: SysLog): SysLogModel {
        val model = SysLogModel()
        BeanUtils.copyProperties(entity, model)
        return model
    }

    override fun copyProperties(ori: SysLog, tar: SysLog) {
        BeanUtils.copyProperties(ori, tar)
    }

    override fun castEntity(model: AddSysLogModel): SysLog {
        val entity = SysLog()
        BeanUtils.copyProperties(model, entity)
        return entity
    }

}
