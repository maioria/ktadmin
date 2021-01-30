package com.prejade.ktadmin.modules.monitor.model

import com.prejade.ktadmin.common.DateUtils
import java.util.*

class OnlineUserModel {
    var id: Int? = null
    lateinit var username: String
    var nickname: String? = null
    var dept: String? = null
    var browser: String? = null
    var ip: String? = null
    var address: String? = null
    var key: String? = null
    var loginTime: Date = DateUtils.getCurrentTime()
    lateinit var token: String
    var encryptToken: String? = null
}
