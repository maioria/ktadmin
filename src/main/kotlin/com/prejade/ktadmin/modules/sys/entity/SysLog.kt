package com.prejade.ktadmin.modules.sys.entity

import com.prejade.ktadmin.common.DateUtils
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SysLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
    var username: String? = null
    var description: String? = null
    var method: String? = null
    var params: String? = null
    var ip: String? = null
    var agent: String? = null
    var address: String? = null
    var browser: String? = null
    var time: Long? = null
    var createTime: Timestamp = DateUtils.getCurrentTimestamp()
}
