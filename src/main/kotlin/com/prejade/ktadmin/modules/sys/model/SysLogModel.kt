package com.prejade.ktadmin.modules.sys.model

import com.prejade.ktadmin.common.Ip2Region
import com.prejade.ktadmin.common.ServletUtils
import javax.servlet.http.HttpServletRequest

class MethodLogModel {
    constructor(
        username: String, description: String,
        method: String?, params: String?, request: HttpServletRequest
    ) {
        this.username = username
        this.description = description
        this.method = method
        this.params = params
        this.ip = ServletUtils.getIp(request)
        if (this.ip != null) {
            this.address = Ip2Region.parseIp(this.ip)
        }
        this.browser = ServletUtils.getBrowser(request)
        this.agent = request.getHeader("User-Agent")
    }

    constructor()

    var address: String? = null
    var username: String? = null
    var browser: String? = null
    var description: String? = null
    var method: String? = null
    var params: String? = null
    var time: Long = System.currentTimeMillis()
    var ip: String? = null
    var agent: String? = null
    var id: String? = null
    var exceptionDetail: ByteArray? = null
    override fun toString(): String {
        return "MethodLogModel(address=$address, username=$username, browser=$browser, description=$description, method=$method, params=$params, time=$time, ip=$ip, agent=$agent, id=$id)"
    }
}
