package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.PasswordUtils
import com.prejade.ktadmin.SecurityUtils
import com.prejade.ktadmin.common.Status
import com.prejade.ktadmin.modules.sys.entity.SysUser
import com.prejade.ktadmin.modules.sys.model.SysUserModel
import com.prejade.ktadmin.modules.sys.service.SysUserService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class MainService(
    private val sysUserService: SysUserService,
    private val authenticationManager: AuthenticationManager
) {
    /**
     * 验证登录是否成功
     */
    fun login(username: String, password: String, ip: String?): SysUser? {
        val sysUser = sysUserService.getByUsername(username) ?: return null
        val result = PasswordUtils.matches(password, sysUser.salt, sysUser.password)
        if (result) {
            sysUserService.refreshLoginInfo(sysUser, ip)
        }
        return sysUser
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(id: Int): SysUserModel {
        return sysUserService.getModel(id)
    }

    /**
     * 生成token
     */
    fun initToken(request: HttpServletRequest, username: String, password: String): String {
        return SecurityUtils.login(request, username, password, authenticationManager).token!!
    }

    /**
     * 重置密码
     */
    fun resetPassword(userId: Int, password: String) {
        sysUserService.modifyPassword(userId, password)
    }

    /**
     * 冻结用户
     */
    fun disabled(username: String): Boolean {
        val user = sysUserService.getByUsername(username) ?: return false
        return user.status == Status.DISABLED
    }
}
