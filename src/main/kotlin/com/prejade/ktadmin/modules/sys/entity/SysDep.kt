package com.prejade.ktadmin.modules.sys.entity

import com.prejade.ktadmin.common.Status
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

/**
 * 部门
 */
@Entity
class SysDep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(nullable = false)
    lateinit var name: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    var parent: SysDep? = null

    @Column(nullable = true, unique = true)
    var fullPath: String? = null

    @Column(nullable = false)
    var createTime: Timestamp = Timestamp(Date().time)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status = Status.NORMAL
}
