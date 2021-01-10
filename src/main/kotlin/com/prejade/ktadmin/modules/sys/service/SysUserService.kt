package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.PasswordUtils
import com.prejade.ktadmin.KtadminException
import com.prejade.ktadmin.common.*
import com.prejade.ktadmin.modules.sys.dao.SysUserRepository
import com.prejade.ktadmin.modules.sys.entity.SysUser
import com.prejade.ktadmin.modules.sys.convert.SysUserConvert
import com.prejade.ktadmin.modules.sys.model.*
import com.prejade.ktadmin.modules.sys.enumes.RoleDataPermissionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SysUserService(
    private val repository: SysUserRepository,
    private val convert: SysUserConvert,
    private val depService: SysDepService
) : BaseService<SysUser, Int>() {
    override fun getRepository(): JpaRepository<SysUser, Int> {
        return repository
    }

    fun getByUsername(username: String): SysUser? {
        return repository.getByUsername(username)
    }

    fun getDepId(id: Int): Int {
        return get(id).dep.id!!
    }

    fun getDataPermissionModel(user: SysUser): DataPermissionModel {
        val result = DataPermissionModel()
        result.users.add(user.id!!)
        for (role in user.roles) {
            when (role.dataPermissionType) {
                RoleDataPermissionType.ALL -> {
                    result.all = true
                }
                RoleDataPermissionType.DEPARTMENTS -> {
                    result.deps.addAll(depService.findSubIds(user.dep.id!!))
                }
                RoleDataPermissionType.DEPARTMENT -> {
                    result.deps.add(user.dep.id!!)
                }
                RoleDataPermissionType.USER -> {
                    result.users.add(user.id!!)
                }
                RoleDataPermissionType.SETTING -> {
                    result.deps.addAll(role.getDepIdDataPermission())
                }
            }
        }
        return result
    }

    fun modifyPassword(userId: Int, password: String) {
        val sysUser = get(userId)
        sysUser.password = PasswordUtils.encode(password, sysUser.salt)
        save(sysUser)
    }

    fun findList(
        dataPermissionModel: DataPermissionModel,
        depId: Int?,
        name: String?,
        status: String?,
        pageNo: Int?,
        pageSize: Int?
    ): PageModel<SysUserModel> {
        return convert.castModel(
            repository.findList(
                dataPermissionModel,
                depId,
                name,
                status,
                PageBuilder.of(pageNo, pageSize)
            )
        )
    }

    fun resetPwd(userId: Int, password: String) {
        val entity = get(userId)
        entity.password = PasswordUtils.encode(password, entity.salt)
        save(entity)
    }

    fun getModel(id: Int): SysUserModel {
        return convert.castModel(get(id))
    }

    fun add(add: AddUser): SysUser {
        val entity = convert.castEntity(add)
        entity.salt = PasswordUtils.getSalt()
        entity.password = PasswordUtils.encode(entity.salt, entity.salt)
        save(entity)
        return entity
    }

    fun upd(id: Int, upd: AddUser) {
        val entity = get(id)
        if (entity.username == "admin") throw KtadminException("不可以编辑管理员")
        val target = convert.castEntity(upd)
        convert.copyProperties(entity, target)
        save(entity)
    }

    fun refreshLoginInfo(entity: SysUser, ip: String?) {
        entity.lastLoginTime = DateUtils.getCurrentTime()
        entity.lastLoginIp = ip
    }

    fun updAccount(id: Int, account: UpdAccount) {
        val entity = get(id)
        entity.name = account.name
        entity.mobile = account.mobile
        entity.tel = account.tel
        entity.email = account.email
        entity.introduction = account.introduction
    }

    fun checkMobileExist(mobile: String, id: Int?): ExistUser? {
        val entity = repository.findByMobileAndStatus(mobile, Status.NORMAL) ?: return null
        if (id != null && id == entity.id) return null
        return convert.castExistModel(entity)
    }

    fun checkEmailExist(email: String, id: Int?): ExistUser? {
        val entity = repository.findByEmailAndStatus(email, Status.NORMAL) ?: return null
        if (id != null && id == entity.id) return null
        return convert.castExistModel(entity)
    }
}
