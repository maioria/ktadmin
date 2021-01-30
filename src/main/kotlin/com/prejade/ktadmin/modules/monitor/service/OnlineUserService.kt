package com.prejade.ktadmin.modules.monitor.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.prejade.ktadmin.JwtTokenUtils
import com.prejade.ktadmin.SysConstant
import com.prejade.ktadmin.clients.CacheClient
import com.prejade.ktadmin.common.*
import com.prejade.ktadmin.modules.monitor.model.OnlineUserModel
import com.prejade.ktadmin.modules.sys.entity.SysUser
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

/**
 * 在线用户service
 */
@Service
class OnlineUserService(
    private val cacheClient: CacheClient,
    private val objectMapper: ObjectMapper
) {
    /**
     * 新增在线用户
     */
    fun addOnlineUser(user: SysUser, token: String, request: HttpServletRequest) {
        val ip = ServletUtils.getIp(request)
        val onlineUser = OnlineUserModel()
        onlineUser.id = user.id
        onlineUser.username = user.username
        onlineUser.nickname = user.nickname
        onlineUser.dept = user.dep.name
        onlineUser.ip = ip
        onlineUser.address = Ip2Region.parseIp(ip)
        onlineUser.browser = ServletUtils.getBrowser(request)
        onlineUser.token = token
        cacheClient.set(
            SysConstant.getOnlineUserKey(onlineUser.token),
            objectMapper.writeValueAsString(onlineUser),
            JwtTokenUtils.EXPIRE_TIME
        )
    }

    fun checkOnline(token: String): Boolean {
        return cacheClient.get(SysConstant.getOnlineUserKey(token)) != null
    }

    fun getOnlineUserByKey(key: String): OnlineUserModel? {
        val value = cacheClient.get(key) ?: return null
        return objectMapper.readValue(value, OnlineUserModel::class.java)
    }

    fun removeOnlineUser(token: String) {
        cacheClient.del(SysConstant.getOnlineUserKey(token))
    }

    fun pageData(pageNo: Int, pageSize: Int, keyword: String?): PageModel<OnlineUserModel> {
        val result = PageModel<OnlineUserModel>()
        result.pageNo = pageNo
        result.pageSize = pageSize
        val list = mutableListOf<OnlineUserModel>()
        val onlineKeys = cacheClient.getKeysByPrefix(SysConstant.getOnlineUserKeyPrefix())
        onlineKeys.filter { cacheClient.get(it) != null && cacheClient.get(it)!!.contains(it) }
        result.total = onlineKeys.size.toLong()
        for (key in onlineKeys) {
            val user = getOnlineUserByKey(key) ?: continue
            list.add(user)
        }
        list.sortByDescending { it.loginTime }

        val fromIndex = (pageNo - 1) * pageSize
        var toIndex = pageNo * pageSize
        if (toIndex > list.size) toIndex = list.size
        result.data = list.subList(fromIndex, toIndex)
        //加入加密的token
        for (online in result.data as MutableList<OnlineUserModel>) {
            online.token = EncryptUtils.desEncrypt(online.token)
        }
        return result
    }

    fun kickOut(keys: String) {
        for (key in keys.split(",")) {
            removeOnlineUser(EncryptUtils.desDecrypt(key))
        }
    }

}
