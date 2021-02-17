package com.prejade.ktadmin.modules.sys.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

class SysUserModel {
    var id: Int? = null
    lateinit var username: String
    lateinit var nickname: String
    var mobile: String? = null
    var email: String? = null
    var tel: String? = null
    var avatar: String? = null
    var lastLoginIp: String? = null

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var lastLoginTime: Date? = null

    @JsonFormat(pattern = "yyyy-MM-dd")
    var entryDate: Date? = null
    var leaveStatus: String? = null

    @JsonFormat(pattern = "yyyy-MM-dd")
    var leaveDate: Date? = null
    var creatorId: String? = null
    var merchantCode: String? = null
    var status: String? = null
    var statusName: String? = null
    var createTime: String? = null
    var roles: Set<String> = setOf()
    var roleLabels: Set<String> = setOf()
    var permissions: Set<String> = setOf()
    var depId: Int? = null
    var depName: String? = null
    var roleNames: String? = null
    var introduction: String? = null
}

class AddUser {
    var id: Int? = null
    lateinit var username: String
    lateinit var nickname: String
    lateinit var mobile: String
    var email: String? = null
    var depId: Int? = null
    var tel: String? = null

    @JsonFormat(pattern = "yyyy-MM-dd")
    var entryDate: Date? = null
    lateinit var roles: String
}

class UpdAccount {
    lateinit var name: String
    lateinit var mobile: String
    var tel: String? = null
    var email: String? = null
    var introduction: String? = null
}

class ExistUser {
    var id: Int? = null
    lateinit var name: String
    var depId: Int? = null
    var depName: String? = null
    lateinit var mobile: String
    var tel: String? = null
    var email: String? = null
}
