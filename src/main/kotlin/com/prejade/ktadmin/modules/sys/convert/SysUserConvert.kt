package com.prejade.ktadmin.modules.sys.convert

import com.prejade.ktadmin.common.BaseConvert
import com.prejade.ktadmin.modules.sys.entity.SysUser
import com.prejade.ktadmin.modules.sys.model.AddUser
import com.prejade.ktadmin.modules.sys.model.ExistUser
import com.prejade.ktadmin.modules.sys.model.SysUserModel
import com.prejade.ktadmin.modules.sys.service.SysDepService
import com.prejade.ktadmin.modules.sys.service.SysRoleService
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class SysUserConvert : BaseConvert<SysUser, SysUserModel, AddUser>() {

    @Autowired
    lateinit var sysDepService: SysDepService

    @Autowired
    lateinit var sysRoleService: SysRoleService
    override fun castModel(entity: SysUser): SysUserModel {
        val model = SysUserModel()
        BeanUtils.copyProperties(entity, model)
        model.roles = entity.getRoleNames()
        model.roleLabels = sysRoleService.castLabels(model.roles)
        model.permissions = entity.getPermissionNames()
        model.depId = entity.dep.id
        model.depName = entity.dep.name
        model.status = entity.status.name
        model.statusName = entity.status.nameValue
        return model
    }

    override fun copyProperties(ori: SysUser, tar: SysUser) {
        ori.nickname = tar.nickname
        ori.mobile = tar.mobile
        ori.tel = tar.tel
        ori.email = tar.email
        ori.roles = tar.roles
        ori.dep = tar.dep
    }

    override fun castEntity(model: AddUser): SysUser {
        val entity = SysUser()
        BeanUtils.copyProperties(model, entity)
        entity.roles = sysRoleService.findByNames(model.roles.split(","))
        entity.dep = sysDepService.get(model.depId!!)
        return entity
    }

    fun castExistModel(entity: SysUser): ExistUser {
        val result = ExistUser()
        BeanUtils.copyProperties(entity, result)
        result.depId = entity.dep.id
        result.depName = entity.dep.name
        return result
    }

}
