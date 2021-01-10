package com.prejade.ktadmin.modules.main.service

import com.prejade.ktadmin.JwtTokenUtils
import com.prejade.ktadmin.PasswordUtils
import com.prejade.ktadmin.SecurityUtils
import com.prejade.ktadmin.SysConstant
import com.prejade.ktadmin.common.CacheClient
import com.prejade.ktadmin.common.Status
import com.prejade.ktadmin.modules.sys.model.SysUserModel
import com.prejade.ktadmin.modules.sys.service.SysUserService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
class MainService(
    val sysUserService: SysUserService,
    val authenticationManager: AuthenticationManager,
    val cacheClient: CacheClient
) {
    /**
     * 验证登录是否成功
     */
    fun login(username: String, password: String, ip: String?): Boolean {
        val sysUser = sysUserService.getByUsername(username) ?: return false
        val result = PasswordUtils.matches(password, sysUser.salt, sysUser.password)
        if (result) {
            sysUserService.refreshLoginInfo(sysUser, ip)
        }
        return result
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
     * 设置登录token
     */
    fun setLoginToken(username: String, token: String) {
        cacheClient.set(SysConstant.getLoginTokenKey(username), token, JwtTokenUtils.EXPIRE_TIME)
    }

    /**
     * 移除登录token
     */
    fun removeLoginToken(username: String) {
        cacheClient.del(SysConstant.getLoginTokenKey(username))
    }

    /**
     * 检查是否是当前登录token
     * 当此用户在别一地方登录时，token会更新
     */
    fun checkLoginToken(username: String, token: String): Boolean {
        return Objects.equals(cacheClient.get(SysConstant.getLoginTokenKey(username)), token)
    }

    /**
     * 设置登录token
     */
    fun setOnlineToken(token: String) {
        cacheClient.set(SysConstant.getOnlineUserKey(token), "true", JwtTokenUtils.EXPIRE_TIME)
    }

    /**
     * 清空登录token
     */
    fun clearOnlineToken(token: String) {
        cacheClient.set(SysConstant.getOnlineUserKey(token), "false", JwtTokenUtils.EXPIRE_TIME)
    }

    /**
     * 检查是否是登录状态
     */
    fun checkOnline(token: String): Boolean {
        val online = cacheClient.get(SysConstant.getOnlineUserKey(token))
        return online != null && Objects.equals(online, "true")
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
