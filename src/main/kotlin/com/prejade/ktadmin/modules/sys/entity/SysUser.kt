package com.prejade.ktadmin.modules.sys.entity

import com.prejade.ktadmin.common.DateUtils
import com.prejade.ktadmin.common.Status
import java.sql.Timestamp
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList

@Entity
class SysUser() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(unique = true, nullable = false)
    lateinit var username: String

    @Column(nullable = false)
    lateinit var nickname: String

    @Column(nullable = false)
    lateinit var salt: String

    @Column(nullable = false)
    lateinit var password: String

    @Column(unique = true)
    lateinit var mobile: String

    @Column(unique = true)
    var tel: String? = null

    @Column(unique = true)
    var email: String? = null

    var avatar: String? = null

    var introduction: String? = null

    var lastLoginIp: String? = null

    var lastLoginTime: Date? = null


    @ManyToOne
    @JoinColumn(name = "dep_id", nullable = false)
    lateinit var dep: SysDep

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sys_user_role",
        joinColumns = [JoinColumn(name = "sys_user_id")],
        inverseJoinColumns = [JoinColumn(name = "sys_role_id")]
    )
    var roles: List<SysRole> = ArrayList()

    @Column(nullable = false)
    var createTime: Timestamp = DateUtils.getCurrentTimestamp()

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var status: Status = Status.NORMAL

    @Transient
    fun getRoleNames(): List<String> {
        val result = mutableListOf<String>()
        for (role in roles) {
            result.add(role.name)
        }
        return result
    }

    @Transient
    fun getPermissionNames(): List<String> {
        val result = mutableListOf<String>()
        for (role in roles) {
            for (permission in role.permissions) result.add(permission.name)
        }
        return result
    }
}
